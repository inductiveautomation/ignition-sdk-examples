package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonParser;
import com.inductiveautomation.ignition.common.lifecycle.Lifecycle;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.secrets.*;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * An example implementation of a Secret Provider that uses MongoDB as the backend data store.
 * <p>
 * This class demonstrates how to implement the {@link SecretProvider} interface and manage secrets
 * stored in a MongoDB database. It supports listing secrets and reading individual secrets by name.
 * <p>
 * The secrets are stored in a MongoDB collection named "secrets", where each document contains
 * a "name" field for the secret name and a "ciphertext" field for the encrypted secret value. The encrypted
 * secret value is stored as a JSON-encoded object, which is decrypted using Ignition's
 * {@link SystemEncryptionService} when reading the secret. The {@link SystemEncryptionService} should also
 * be used to populate the document when writing secrets to the database, which must be done externally at this
 * time.
 * <p>
 * The class also implements the {@link Lifecycle} interface, allowing it to manage its own lifecycle
 * and resources via the startup and shutdown methods. It's an optional interface to implement, so
 * only do so if you need to manage resources explicitly. In this case, the MongoDB client is created
 * when the provider is first used and remains open for the lifetime of the provider.
 */
public class MongoDbSecretProvider implements SecretProvider, Lifecycle {

    // The names of the MongoDB collections used in this example.
    private static final String COLLECTION_SECRETS = "secrets";

    // The keys used in the MongoDB documents.
    private static final String KEY_NAME = "name";
    private static final String KEY_CIPHERTEXT = "ciphertext";

    // Instance fields for this class.
    private final SecretProviderContext context;
    private final MongoDbSecretProviderResource settings;
    private MongoClientSettings mongoClientSettings;
    private MongoClient mongoClient;
    private MongoDatabase database;

    /**
     * Constructor for the MongoDbSecretProvider.
     *
     * @param context  the {@link SecretProviderContext} encapsulating the contextual information needed for creating
     *                 new {@link SecretProvider} instances of this type.
     * @param settings the {@link MongoDbSecretProviderResource} containing the configuration settings for this
     *                 provider.
     */
    MongoDbSecretProvider(SecretProviderContext context, MongoDbSecretProviderResource settings) {
        this.context = context;
        this.settings = settings;
    }

    @Override
    public void startup() {
        // Create a builder for the MongoDB client settings using the provided connection string.
        MongoClientSettings.Builder builder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(settings.connectionString()));

        // Enable authentication if username and password are provided.
        if (StringUtils.isNotBlank(settings.username()) && settings.password() != null) {
            try (Plaintext plaintext = Secret.create(context.getGatewayContext(), settings.password()).getPlaintext()) {
                builder.credential(
                        MongoCredential.createCredential(
                                settings.username(),
                                settings.authenticationDb(),
                                plaintext.getAsString(StandardCharsets.UTF_8).toCharArray())
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to create MongoDB credential", e);
            }
        }

        this.mongoClientSettings = builder.build();
    }

    @Override
    public void shutdown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Retrieves the MongoDB database instance for this secret provider.
     *
     * @return the {@link MongoDatabase} instance.
     */
    private MongoDatabase getDatabase() {
        if (database == null) {
            if (mongoClient == null) {
                // We don't use a try-with-resources block because we want the client to remain open for the lifetime
                // of this user source profile.
                mongoClient = MongoClients.create(mongoClientSettings);
            }

            // Get the database from the MongoDB client using the configured database name.
            database = mongoClient.getDatabase(settings.databaseName());
        }
        return database;
    }

    @Override
    public List<String> list() throws SecretProviderException {
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_SECRETS);

        try {
            return collection.find()
                    .map(doc -> doc.getString(KEY_NAME))
                    .into(new java.util.ArrayList<>());
        } catch (Exception e) {
            context.getLog().error("Failed to list secrets from MongoDB", e);
            throw new SecretProviderException("Failed to list secrets", e);
        }
    }

    @Override
    public Plaintext read(String s) throws SecretProviderException {
        Objects.requireNonNull(s, "Secret name cannot be null");
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_SECRETS);

        // Search for the secret by name.
        Document doc;
        try {
            doc = collection.find(new Document(KEY_NAME, s)).first();
        } catch (Exception e) {
            context.getLog().error("Failed to read secret '" + s + "' from MongoDB", e);
            throw new SecretProviderException("Failed to read secret", e);
        }

        // If the secret was not found, throw an exception.
        if (doc == null) {
            throw new SecretNotFoundException("Secret '" + s + "' does not exist");
        }

        // Decrypt the ciphertext using the system encryption service.
        try {
            JsonElement element = JsonParser.parseString(doc.get(KEY_CIPHERTEXT, Document.class).toJson());
            return context.getGatewayContext().getSystemEncryptionService().decryptFromJson(element);
        } catch (Exception e) {
            context.getLog().error("Failed to decrypt secret '" + s + "'", e);
            throw new SecretProviderException("Failed to decrypt secret", e);
        }
    }
}
