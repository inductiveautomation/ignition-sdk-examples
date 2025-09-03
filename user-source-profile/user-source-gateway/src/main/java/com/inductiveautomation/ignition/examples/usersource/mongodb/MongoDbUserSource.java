package com.inductiveautomation.ignition.examples.usersource.mongodb;

import com.inductiveautomation.ignition.common.config.BasicConfigurationProperty;
import com.inductiveautomation.ignition.common.config.ConfigurationProperty;
import com.inductiveautomation.ignition.common.gui.UICallback;
import com.inductiveautomation.ignition.common.role.BasicRole;
import com.inductiveautomation.ignition.common.role.Role;
import com.inductiveautomation.ignition.common.user.*;
import com.inductiveautomation.ignition.common.user.schedule.ScheduleAdjustment;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.authentication.impl.PasswordExpirationBypass;
import com.inductiveautomation.ignition.gateway.secrets.Plaintext;
import com.inductiveautomation.ignition.gateway.secrets.Secret;
import com.inductiveautomation.ignition.gateway.user.AbstractUserSourceProfile;
import com.inductiveautomation.ignition.gateway.user.PasswordExpiredException;
import com.inductiveautomation.ignition.gateway.user.UserSourceManager;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfileKernel;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An example implementation of a User Source Profile that uses MongoDB as the backend data store.
 * <p>
 * This class demonstrates how to implement user authentication, role management, and user management
 * using a MongoDB database. It includes methods for adding, altering, and removing users and roles,
 * as well as authenticating users via username/password or badge.
 * <p>
 * Note: This is a simplified example for demonstration purposes and not intended for use in a production environment.
 * <p>
 * Some notable omissions in this example include:
 * <ul>
 * <li>Password hashing (passwords are stored in plain text for simplicity)</li>
 * <li>Password complexity (min length, char classes, etc.)</li>
 * <li>Input validation and sanitization</li>
 * <li>Comprehensive error handling (particularly in network connectivity) and logging</li>
 * <li>Configuration options for MongoDB connection (e.g., SSL)</li>
 * </ul>
 */
public class MongoDbUserSource extends AbstractUserSourceProfile {

    private static final LoggerEx LOGGER = LoggerEx.newBuilder().build(MongoDbUserSource.class);

    // Custom configuration properties for this user source profile.
    public static final ConfigurationProperty<String> FAVORITE_COLOR =
            new BasicConfigurationProperty<>("favoriteColor", "MongoDbUserSource.favoriteColor.Desc",
                    "", String.class, "blue");
    public static final ConfigurationProperty<Integer> FAVORITE_NUMBER =
            new BasicConfigurationProperty<>("favoriteNumber", "MongoDbUserSource.favoriteNumber.Desc",
                    "", Integer.class, 42);
    public static final ConfigurationProperty<Boolean> LIKES_APPLES =
            new BasicConfigurationProperty<>("likesApples", "MongoDbUserSource.likesApples.Desc",
                    "", Boolean.class, true);

    // The names of the MongoDB collections used in this example.
    private static final String COLLECTION_ROLES = "roles";
    private static final String COLLECTION_USERS = "users";

    // The keys used in the MongoDB documents.
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PASSWORD_DATE = "passwordDate";
    private static final String KEY_PASSWORD_HISTORY = "passwordHistory";
    private static final String KEY_LASTNAME = "lastName";
    private static final String KEY_FIRSTNAME = "firstName";
    private static final String KEY_SCHEDULE = "schedule";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_BADGE = "badge";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_ROLES = "roles";
    private static final String KEY_SA = "scheduleAdjustments";
    private static final String KEY_AVAILABLE = "available";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";
    private static final String KEY_NOTE = "note";
    private static final String KEY_CI = "contactInfo";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VALUE = "value";
    private static final String KEY_FAVORITE_COLOR = "favoriteColor";
    private static final String KEY_FAVORITE_NUMBER = "favoriteNumber";
    private static final String KEY_LIKES_APPLES = "likesApples";

    // Instance fields for this class.
    private MongoClientSettings mongoClientSettings;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private final MongoDbUserSourceResource settings;

    /**
     * Constructor for the {@link MongoDbUserSource}.
     *
     * @param kernel the UserSourceProfileKernel that provides the context for this profile.
     * @param settings the {@link MongoDbUserSourceResource} containing configuration settings.
     */
    MongoDbUserSource(UserSourceProfileKernel kernel, MongoDbUserSourceResource settings) {
        super(kernel);
        this.settings = settings;
    }

    @Override
    public void startup(UserSourceManager manager) {
        super.startup(manager);

        // Create a builder for the MongoDB client settings using the provided connection string.
        MongoClientSettings.Builder builder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(settings.connectionString()));

        // Enable authentication if username and password are provided.
        if (StringUtils.isNotBlank(settings.username()) && settings.password() != null) {
            try (Plaintext plaintext = Secret.create(manager.getGatewayContext(), settings.password()).getPlaintext()) {
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
        super.shutdown();

        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Retrieves the MongoDB database instance for this user source profile.
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
    public AuthenticatedUser authenticate(AuthChallenge challenge) throws Exception {
        return authenticate(challenge, false);
    }

    /**
     * Authenticates a user based on the provided authentication challenge.
     *
     * @param challenge        the authentication challenge containing credentials.
     * @param bypassExpiration whether to bypass password expiration checks.
     * @return an AuthenticatedUser if authentication is successful, null otherwise.
     * @throws Exception if an error occurs during authentication.
     */
    private AuthenticatedUser authenticate(AuthChallenge challenge, boolean bypassExpiration) throws Exception {
        // Handle any of the authentication challenges that this user source supports.
        if (challenge instanceof SimpleAuthChallenge usernameAndPassword) {
            return authenticateUsernamePassword(usernameAndPassword, bypassExpiration);
        } else if (challenge instanceof BadgeAuthChallenge badgeChallenge) {
            return authenticateBadge(badgeChallenge, bypassExpiration);
        } else if (challenge instanceof PasswordExpirationBypass wrapped) {
            return authenticate(wrapped.actual(), true);
        } else {
            throw new Exception("Authentication using username and password or badge is required.");
        }
    }

    /**
     * Authenticates a user using username and password.
     *
     * @param challenge        the authentication challenge containing username and password.
     * @param bypassExpiration whether to bypass password expiration checks.
     * @return an AuthenticatedUser if authentication is successful, null otherwise.
     * @throws Exception if an error occurs during authentication.
     */
    private AuthenticatedUser authenticateUsernamePassword(SimpleAuthChallenge challenge,
                                                           boolean bypassExpiration) throws Exception {

        // Check if the user is locked out.
        String uname = challenge.username();
        if (isLockedOut(uname)) {
            LogUtil.logOncePerMinute(
                    LOGGER,
                    Level.INFO,
                    Level.DEBUG,
                    String.format("User '%s' is locked out", uname)
            );
            return null;
        }

        try {
            // Validate the user exists
            MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_USERS);
            Document document = collection.find(Filters.eq(KEY_NAME, uname)).first();
            if (document != null) {
                // Found the user, now validate the password
                if (isPasswordInvalid(document, challenge.password(), bypassExpiration)) {
                    return null;
                }

                return new BasicAuthenticatedUser(toUser(document), new Date());
            } else {
                return null;
            }
        } catch (PasswordExpiredException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("Unexpected exception during internal authenticator authentication.", ex);
        }
    }

    /**
     * Checks if the provided password is valid for the user document.
     *
     * @param document         the user document from the database.
     * @param pwd              the password to validate.
     * @param bypassExpiration whether to bypass password expiration checks.
     * @return true if the password is invalid, false otherwise.
     * @throws PasswordExpiredException if the password is expired and bypassExpiration is false.
     */
    private boolean isPasswordInvalid(Document document, String pwd, boolean bypassExpiration)
            throws PasswordExpiredException {
        String uname = document.getString(KEY_NAME);
        if (!StringUtils.equals(pwd, document.getString(KEY_PASSWORD))) {
            if (notifyFailedAttempt(uname)) {
                LogUtil.logOncePerMinute(
                        LOGGER,
                        Level.INFO,
                        Level.DEBUG,
                        String.format("User '%s' is now locked out", uname)
                );
            }
            return true;
        }

        // Password is valid, now check if the password is expired
        if (settings.passwordMaxAge() > 0 && !bypassExpiration) {
            long pwdTimestamp = document.getLong(KEY_PASSWORD_DATE);
            if (pwdTimestamp > 0) {
                DateTime passwordCreatedOn = new DateTime(pwdTimestamp);
                DateTime now = DateTime.now();
                int days = Days.daysBetween(passwordCreatedOn.toLocalDate(), now.toLocalDate()).getDays();
                if (days > settings.passwordMaxAge()) {
                    throw new PasswordExpiredException(getName(), uname);
                }
            }
        }

        return false;
    }

    /**
     * Authenticates a user using a badge.
     *
     * @param challenge        the badge authentication challenge.
     * @param bypassExpiration whether to bypass password expiration checks.
     * @return an AuthenticatedUser if authentication is successful, null otherwise.
     * @throws Exception if an error occurs during authentication.
     */
    private AuthenticatedUser authenticateBadge(BadgeAuthChallenge challenge,
                                                boolean bypassExpiration) throws Exception {
        String badge = challenge.badge();
        try {
            // Validate the user exists
            MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_USERS);

            // Find all users with the specified badge
            FindIterable<Document> findIterable = collection.find(Filters.eq(KEY_BADGE, badge));
            List<Document> matches = new ArrayList<>();
            for (Document doc : findIterable) {
                matches.add(doc);
            }

            // We should only have one user with a given badge; otherwise, the badge is ambiguous.
            if (matches.isEmpty()) {
                return null;
            } else if (matches.size() == 1) {
                Document document = matches.get(0);

                // Found the user, now validate user is not locked out.
                String uname = document.getString(KEY_NAME);
                if (isLockedOut(uname)) {
                    LogUtil.logOncePerMinute(
                            LOGGER,
                            Level.INFO,
                            Level.DEBUG,
                            String.format("User '%s' is locked out", uname)
                    );
                    return null;
                }

                // Validate the secret if provided
                if (challenge.hasSecret() && isPasswordInvalid(document, challenge.secret(), bypassExpiration)) {
                    return null;
                }

                return new BasicAuthenticatedUser(toUser(document), new Date());
            } else {
                String badgeUsers = matches.stream()
                        .map(doc -> doc.getString(KEY_NAME))
                        .collect(Collectors.joining(", ", "'", "'"));
                LogUtil.logOncePerMinute(LOGGER, Level.WARN, Level.DEBUG, String.format(
                        "User with badge '%s' is ambiguous - could be one of: %s",
                        badge,
                        badgeUsers)
                );
                return null;
            }
        } catch (PasswordExpiredException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("Unexpected exception during internal authenticator authentication.", ex);
        }
    }

    @Override
    public void addRole(Role role, UICallback ui) {
        if (!role.getProfileName().equals(getProfileName())) {
            ui.warn("User source does not match role. Unexpected results may occur.");
        }

        // Validate the role doesn't already exist
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_ROLES);
        if (collection.find(Filters.eq(KEY_NAME, role.getName())).first() != null) {
            throw new IllegalArgumentException("Role with name '" + role.getName() + "' already exists.");
        }

        // Create a new role document from the role.
        Document document = new Document()
                .append(KEY_ID, UUID.randomUUID().toString())
                .append(KEY_NAME, role.getName())
                .append(KEY_NOTES, role.getNotes());
        collection.insertOne(document);
    }

    @Override
    public void alterRole(Role role, UICallback ui) {
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_ROLES);

        // Validate the role already exists
        Bson filter = Filters.eq(KEY_ID, role.getId());
        Document document = collection.find(filter).first();
        if (document == null) {
            throw new IllegalArgumentException("Cannot alter role: role with ID '" + role.getId() + "' not found.");
        }

        // Does a role with the specified name exist already if we are changing names?
        Bson filter2 = Filters.and(Filters.ne(KEY_ID, role.getId()), Filters.eq(KEY_NAME, role.getName()));
        if (collection.find(filter2).first() != null) {
            throw new IllegalArgumentException("Cannot alter role: role with name '"
                    + role.getName() + "' already exists.");
        }

        // Update the role document with the new values.
        Bson update = Updates.combine(
                Updates.set(KEY_NAME, role.getName()),
                Updates.set(KEY_NOTES, role.getNotes())
        );

        // Perform the update operation.
        collection.updateOne(filter, update);
    }

    @Override
    public void removeRole(Role role, UICallback ui) {
        // Remove the role from all users that have this role
        Bson update = Updates.pull(KEY_ROLES, role.getId());
        getDatabase().getCollection(COLLECTION_USERS).updateMany(Filters.empty(), update);

        // Now remove the role itself.
        DeleteResult result = getDatabase().getCollection(COLLECTION_ROLES).deleteOne(Filters.eq(KEY_ID, role.getId()));
        if (result.getDeletedCount() == 0) {
            throw new IllegalArgumentException("Cannot remove role: role with ID '" + role.getId() + "' not found.");
        }
    }

    @Nonnull
    @Override
    public Collection<Role> getRoles() {
        Collection<Role> roles = new ArrayList<>();
        try (MongoCursor<Document> cursor = getDatabase().getCollection(COLLECTION_ROLES).find().iterator()) {
            while (cursor.hasNext()) {
                roles.add(toRole(cursor.next()));
            }
        }
        return roles;
    }

    @Override
    public void addUser(User user, UICallback ui) {
        if (!user.getProfileName().equals(getProfileName())) {
            ui.warn("User source does not match user. Unexpected results may occur.");
        }

        // Validate the user doesn't already exist
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_USERS);
        if (collection.find(Filters.eq(KEY_NAME, user.get(User.Username))).first() != null) {
            throw new IllegalArgumentException("User with username '" + user.get(User.Username) + "' already exists.");
        }

        // Create a new user document from the user.
        //
        // WARNING: For this example, we are storing the password in plain text (not recommended for production use)
        Document document = new Document()
                .append(KEY_ID, UUID.randomUUID().toString())
                .append(KEY_NAME, user.get(User.Username))
                .append(KEY_PASSWORD, user.get(User.Password))
                .append(KEY_PASSWORD_DATE, System.currentTimeMillis())
                .append(KEY_FIRSTNAME, user.get(User.FirstName))
                .append(KEY_LASTNAME, user.get(User.LastName))
                .append(KEY_BADGE, user.get(User.Badge))
                .append(KEY_NOTES, user.get(User.Notes))
                .append(KEY_LANGUAGE, user.get(User.Language))
                .append(KEY_SCHEDULE, user.get(User.Schedule));

        // If password history is enabled and a password is provided, add the initial password to the history.
        String password = user.get(User.Password);
        if (settings.passwordHistory() > 0 && StringUtils.isNotBlank(password)) {
            document.append(KEY_PASSWORD_HISTORY, List.of(password));
        }

        // Roles, Schedule Adjustments, Contact Info, etc.
        document.append(KEY_ROLES, getRoleIds(user.getRoles()));
        document.append(KEY_CI, getContactInfoDocuments(user));
        document.append(KEY_SA, getScheduleAdjustmentDocuments(user));

        // Custom properties
        document.append(KEY_FAVORITE_COLOR, user.getOrElse(FAVORITE_COLOR, "blue"));
        document.append(KEY_FAVORITE_NUMBER, user.getOrElse(FAVORITE_NUMBER, 42));
        document.append(KEY_LIKES_APPLES, user.getOrElse(LIKES_APPLES, true));

        collection.insertOne(document);
    }

    /**
     * Converts a list of MongoDB Documents into a list of ContactInfo objects.
     *
     * @param documents the list of MongoDB Documents representing the user's contact information.
     * @return a list of ContactInfo objects.
     */
    private List<ContactInfo> getContactInfo(List<Document> documents) {
        List<ContactInfo> contactInfoList = new ArrayList<>();
        for (Document doc : documents) {
            ContactInfo contactInfo = new ContactInfo(doc.getString(KEY_TYPE), doc.getString(KEY_VALUE));
            contactInfoList.add(contactInfo);
        }
        return contactInfoList;
    }

    /**
     * Converts a list of MongoDB Documents into a list of ScheduleAdjustment objects.
     *
     * @param documents the list of MongoDB Documents representing the user's schedule adjustments.
     * @return a list of ScheduleAdjustment objects.
     */
    private List<ScheduleAdjustment> getScheduleAdjustments(List<Document> documents) {
        List<ScheduleAdjustment> adjustments = new ArrayList<>();
        for (Document doc : documents) {
            boolean available = doc.getBoolean(KEY_AVAILABLE);
            Date start = null;
            if (doc.getLong(KEY_START) != null) {
                start = new Date(doc.getLong(KEY_START));
            }
            Date end = null;
            if (doc.getLong(KEY_END) != null) {
                end = new Date(doc.getLong(KEY_END));
            }
            String note = null;
            if (doc.getString(KEY_NOTE) != null) {
                note = doc.getString(KEY_NOTE);
            }
            adjustments.add(new ScheduleAdjustment(start, end, available, note));
        }
        return adjustments;
    }

    /**
     * Converts the user's schedule adjustments into a list of MongoDB Documents.
     *
     * @param user the user whose schedule adjustments are to be converted.
     * @return a list of Documents representing the user's schedule adjustments.
     */
    private List<Document> getScheduleAdjustmentDocuments(User user) {
        List<Document> documents = new ArrayList<>();
        for (ScheduleAdjustment sa : user.getScheduleAdjustments()) {
            Document document = new Document();
            document.append(KEY_AVAILABLE, sa.isAvailable());
            if (sa.getStart() != null) {
                document.append(KEY_START, sa.getStart().getTime());
            }
            if (sa.getEnd() != null) {
                document.append(KEY_END, sa.getEnd().getTime());
            }
            if (sa.getNote() != null) {
                document.append(KEY_NOTE, sa.getNote());
            }
            document.append(KEY_NOTE, sa.getNote());
            documents.add(document);
        }
        return documents;
    }

    /**
     * Converts the user's contact information into a list of MongoDB Documents.
     *
     * @param user the user whose contact information is to be converted.
     * @return a list of Documents representing the user's contact information.
     */
    private List<Document> getContactInfoDocuments(User user) {
        List<Document> documents = new ArrayList<>();
        for (ContactInfo ci : user.getContactInfo()) {
            Document document = new Document();
            document.append(KEY_TYPE, ci.getContactType());
            document.append(KEY_VALUE, ci.getValue());
            documents.add(document);
        }
        return documents;
    }

    /**
     * Retrieves the role IDs for the given role names.
     *
     * @param roleNames the collection of role names to look up.
     * @return a set of role IDs corresponding to the provided role names, or null if no roles are found.
     */
    private Collection<String> getRoleIds(Collection<String> roleNames) {
        List<String> roleIds = new ArrayList<>();

        if (roleNames != null && !roleNames.isEmpty()) {
            MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_ROLES);
            try (MongoCursor<Document> cursor = collection.find(Filters.in(KEY_NAME, roleNames)).iterator()) {
                while (cursor.hasNext()) {
                    String roleId = cursor.next().getString(KEY_ID);
                    if (!roleIds.contains(roleId)) {
                        roleIds.add(roleId);
                    }
                }
            }
        }
        return roleIds.isEmpty() ? null : roleIds;
    }

    /**
     * Retrieves the role names for the given role IDs.
     *
     * @param roleIds the collection of role IDs to look up.
     * @return a list of role names corresponding to the provided role IDs, or null if no roles are found.
     */
    private List<String> getRoleNames(Collection<String> roleIds) {
        List<String> roleNames = new ArrayList<>();

        if (roleIds != null && !roleIds.isEmpty()) {
            MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_ROLES);
            try (MongoCursor<Document> cursor = collection.find(Filters.in(KEY_ID, roleIds)).iterator()) {
                while (cursor.hasNext()) {
                    String roleName = cursor.next().getString(KEY_NAME);
                    if (!roleNames.contains(roleName)) {
                        roleNames.add(roleName);
                    }
                }
            }
        }
        return roleNames.isEmpty() ? null : roleNames;
    }

    @Override
    public void alterUser(User user, UICallback ui) {
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_USERS);

        // Validate the user already exists
        Bson filter = Filters.eq(KEY_ID, user.getId());
        Document document = collection.find(filter).first();
        if (document == null) {
            throw new IllegalArgumentException("Cannot alter user: user with ID '" + user.getId() + "' not found.");
        }

        // Does a user with the specified username exist already if we are changing names?
        Bson filter2 = Filters.and(Filters.ne(KEY_ID, user.getId()), Filters.eq(KEY_NAME, user.get(User.Username)));
        if (collection.find(filter2).first() != null) {
            throw new IllegalArgumentException("Cannot alter user: user with username '"
                    + user.get(User.Username) + "' already exists.");
        }

        // Update the user document with the new values.
        Bson update = Updates.combine(
                Updates.set(KEY_NAME, user.get(User.Username)),
                Updates.set(KEY_FIRSTNAME, user.get(User.FirstName)),
                Updates.set(KEY_LASTNAME, user.get(User.LastName)),
                Updates.set(KEY_BADGE, user.get(User.Badge)),
                Updates.set(KEY_NOTES, user.get(User.Notes)),
                Updates.set(KEY_LANGUAGE, user.get(User.Language)),
                Updates.set(KEY_SCHEDULE, user.get(User.Schedule))
        );

        // If the password is set and has not been used yet, update it and the password date.
        if (StringUtils.isNotBlank(user.get(User.Password))) {
            String newPassword = user.get(User.Password);
            List<String> passwordHistory = checkPasswordHistory(document, user.get(User.Password));

            // Update the password and password date.
            update = Updates.combine(update,
                    Updates.set(KEY_PASSWORD, newPassword),
                    Updates.set(KEY_PASSWORD_DATE, System.currentTimeMillis())
            );

            // If password history is enabled, update it as well.
            if (passwordHistory != null) {
                update = Updates.combine(update, Updates.set(KEY_PASSWORD_HISTORY, passwordHistory));
            }
        }

        // Set roles, contact info, and schedule adjustments
        update = Updates.combine(update, Updates.set(KEY_ROLES, getRoleIds(user.getRoles())));
        update = Updates.combine(update, Updates.set(KEY_CI, getContactInfoDocuments(user)));
        update = Updates.combine(update, Updates.set(KEY_SA, getScheduleAdjustmentDocuments(user)));

        // Set our custom properties
        update = Updates.combine(update, Updates.set(KEY_FAVORITE_COLOR, user.getOrElse(FAVORITE_COLOR, "blue")));
        update = Updates.combine(update, Updates.set(KEY_FAVORITE_NUMBER, user.getOrElse(FAVORITE_NUMBER, 42)));
        update = Updates.combine(update, Updates.set(KEY_LIKES_APPLES, user.getOrElse(LIKES_APPLES, true)));

        // Perform the update operation.
        collection.updateOne(filter, update);
    }

    @Override
    public void removeUser(User user, UICallback ui) {
        DeleteResult result = getDatabase().getCollection(COLLECTION_USERS).deleteOne(Filters.eq(KEY_ID, user.getId()));
        if (result.getDeletedCount() == 0) {
            throw new IllegalArgumentException("Cannot remove user: user with ID '" + user.getId() + "' not found.");
        }
    }

    @Override
    public void alterPassword(User user, String oldPassword, String newPassword) {
        MongoCollection<Document> collection = getDatabase().getCollection(COLLECTION_USERS);

        // Validate the user already exists
        Bson filter = Filters.eq(KEY_ID, user.getId());
        Document document = collection.find(filter).first();
        if (document == null) {
            throw new IllegalArgumentException("User with ID '" + user.getId() + "' does not exist.");
        }

        // Verify the old password matches.
        if (!StringUtils.equals(oldPassword, document.getString(KEY_PASSWORD))) {
            throw new IllegalArgumentException("The old password is incorrect.");
        }

        // If password history is enabled, verify the new password is not in the history.
        List<String> passwordHistory = checkPasswordHistory(document, newPassword);

        // Update the password and password date.
        Bson update = Updates.combine(
                Updates.set(KEY_PASSWORD, newPassword),
                Updates.set(KEY_PASSWORD_DATE, System.currentTimeMillis())
        );
        if (passwordHistory != null) {
            update = Updates.combine(update, Updates.set(KEY_PASSWORD_HISTORY, passwordHistory));
        }

        collection.updateOne(filter, update);
    }

    @Nonnull
    @Override
    public Collection<User> getUsers() {
        Collection<User> users = new ArrayList<>();
        try (MongoCursor<Document> cursor = getDatabase().getCollection(COLLECTION_USERS).find().iterator()) {
            while (cursor.hasNext()) {
                users.add(toUser(cursor.next()));
            }
        }
        return users;
    }

    @Nonnull
    @Override
    public Optional<User> getUser(String userName) {
        return Optional.ofNullable(
                toUser(getDatabase().getCollection(COLLECTION_USERS).find(Filters.eq(KEY_NAME, userName)).first())
        );
    }

    @Override
    public Set<UserSourceEditCapability> getEditFlags() {
        return EnumSet.allOf(UserSourceEditCapability.class);
    }

    /**
     * Checks if the new password has been used in the user's password history.
     *
     * @param document    the user document from the database.
     * @param newPassword the new password to check.
     * @return the new list of previous passwords if password history is enabled, null otherwise.
     * @throws IllegalArgumentException if the new password is found in the user's password history.
     */
    private List<String> checkPasswordHistory(Document document, String newPassword) throws IllegalArgumentException {
        List<String> history = null;
        if (settings.passwordHistory() > 0) {
            history = document.getList(KEY_PASSWORD_HISTORY, String.class);
            if (history.contains(newPassword)) {
                throw new IllegalArgumentException("The new password cannot be the same as any of the last "
                        + settings.passwordHistory() + " passwords.");
            }

            while (history.size() >= settings.passwordHistory()) {
                history.remove(0);
            }
            history.add(newPassword);
        }
        return history;
    }

    /**
     * Converts a MongoDB Document to a {@link BasicRole}.
     *
     * @param document the MongoDB Document representing the role.
     * @return a {@link BasicRole} object or null if the document is null.
     */
    private BasicRole toRole(Document document) {
        if (document == null) {
            return null;
        }

        BasicRole basicRole = new BasicRole(getProfileName(), document.getString(KEY_ID));
        basicRole.setName(document.getString(KEY_NAME));
        basicRole.setNotes(document.getString(KEY_NOTES));
        return basicRole;
    }

    /**
     * Converts a MongoDB Document to a {@link BasicUser}.
     *
     * @param document the MongoDB Document representing the user.
     * @return a {@link BasicUser} object or null if the document is null.
     */
    private BasicUser toUser(Document document) {
        if (document == null) {
            return null;
        }

        String id = document.getString(KEY_ID);
        BasicUser basicUser = new BasicUser(getProfileName(), id, null, null);
        basicUser.set(User.Username, document.getString(KEY_NAME));
        basicUser.set(User.FirstName, document.getString(KEY_FIRSTNAME));
        basicUser.set(User.LastName, document.getString(KEY_LASTNAME));
        basicUser.set(User.Badge, document.getString(KEY_BADGE));
        basicUser.set(User.Notes, document.getString(KEY_NOTES));
        basicUser.set(User.Language, document.getString(KEY_LANGUAGE));
        basicUser.set(User.Schedule, document.getString(KEY_SCHEDULE));

        // Set roles, contact info, and schedule adjustments
        basicUser.setRoles(getRoleNames(document.getList(KEY_ROLES, String.class)));
        basicUser.setContactInfo(getContactInfo(document.getList(KEY_CI, Document.class)));
        basicUser.setScheduleAdjustments(getScheduleAdjustments(document.getList(KEY_SA, Document.class)));

        // Set our custom properties
        basicUser.set(FAVORITE_COLOR, document.getString(KEY_FAVORITE_COLOR));
        basicUser.set(FAVORITE_NUMBER, document.getInteger(KEY_FAVORITE_NUMBER));
        basicUser.set(LIKES_APPLES, document.getBoolean(KEY_LIKES_APPLES));

        return basicUser;
    }
}
