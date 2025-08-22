package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonParser;
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


public class MongoDbSecretProvider implements SecretProvider {

    private static final LoggerEx LOGGER = LoggerEx.newBuilder().build(MongoDbSecretProvider.class);

    // The names of the MongoDB collections used in this example.
    private static final String COLLECTION_SECRETS = "secrets";

    // The keys used in the MongoDB documents.
    private static final String KEY_NAME = "name";
    private static final String KEY_CIPHERTEXT = "ciphertext";

    // Instance fields for this class.
    private final SecretProviderContext context;
    private final MongoDbSecretProviderResource settings;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    MongoDbSecretProvider(SecretProviderContext context, MongoDbSecretProviderResource settings) {
        this.context = context;
        this.settings = settings;

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

        // We don't use a try-with-resources block because we want the client to remain open for the lifetime
        // of this secret provider. If we were to use this in production, we probably would want to re-evaluate
        // this decision. This code could possibly lead to resource leaks if not managed properly or issues with
        // connectivity if the MongoDB instance is restarted or becomes unavailable.
        mongoClient = MongoClients.create(builder.build());
        database = mongoClient.getDatabase(settings.databaseName());
    }

    @Override
    public List<String> list() throws SecretProviderException {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_SECRETS);

        try {
            return collection.find()
                    .map(doc -> doc.getString(KEY_NAME))
                    .into(new java.util.ArrayList<>());
        } catch (Exception e) {
            LOGGER.error("Failed to list secrets from MongoDB", e);
            throw new SecretProviderException("Failed to list secrets", e);
        }
    }

    @Override
    public Plaintext read(String s) throws SecretProviderException {
        Objects.requireNonNull(s, "Secret name cannot be null");
        MongoCollection<Document> collection = database.getCollection(COLLECTION_SECRETS);

        // Search for the secret by name.
        Document doc = null;
        try {
            doc = collection.find(new Document(KEY_NAME, s)).first();
        } catch (Exception e) {
            LOGGER.error("Failed to read secret '" + s + "' from MongoDB", e);
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
            LOGGER.error("Failed to decrypt secret '" + s + "'", e);
            throw new SecretProviderException("Failed to decrypt secret", e);
        }
    }
}
