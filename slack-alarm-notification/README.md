# Slack Alarm Notification Example
### Overview

Provides a framework for implementing a custom alarm notification system.
Users to be notified need to have an outgoing webhook registered through Slack's API, then added to their user profile
in Ignition.

If you have a Slack workspace, and you permissions to create apps in the workspace, you can create/manage
your apps at [api.slack.com/apps](https://api.slack.com/apps). After you create the app and generate the
webhook, you can add the webhook as a `SLACK_WEBHOOK` Contact Type under your user profile, using either
of the following methods:

#### Using the UI
Login to the Gateway, navigate to "Platform > Security > User Sources > [YOUR_USER_SOURCE]," select "Manage Users"
under the dropdown next to the User Source you want to use, select your user and then "Edit," and add the "Slack"
Contact Type where the value is the URL of the webhook.


#### Using The Resource Configuration
1. Navigate to the Ignition install directory, then navigate to the following subdirectory:
    ```bash
    cd $IGNITION_HOME
    cd ./data/config/resources/core/ignition/user-source/$YOUR_USER_SOURCE
    ```

2. Open the `users.json` file, and under the `"users"` key, find the user you want to edit, and add
the following JSON object to the `"contactInfos"` array:
    ```json
    {
      "contactType": "Slack",
      "value": "[YOUR_WEBHOOK_URL]",
      "order": 0
    }
    ```
      
    NB. `"order"` here is the sequence number for the priority of the particular contact type.

    Your `users.json` should look something like this after the above:
    ```jsonc
    {
      "users": [
        {
          //... other user properties
      
          "contactInfos": [
            {
              "contactType": "Slack",
              "value": "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXX",
              "order": 0
            }
          ]
        }
      ]
    }
    ```

3. Restart the Gateway to apply the changes.