# MyTodoApp
This is an Android demo todo application.

Time spent: 24 hours spent in total

Completed user stories:

 Required: User can view the list of todos.
 
 Required: User can add a new todo item which is saved in a local file on the device.
 
 Required: User can delete a todo by long clicking on the item.
 
 Required: User can edit a todo item by clicking on the item. This was done by launching a new intent and passing data to launched intent and returning data result to parent activity.

 Optional: Used string.xml to store 'edit item below' label value
 
 Optional: Persist the todo items into SQLite using ActiveAndroid
 
 Optional: Improve style of the todo items in the list using a custom cursor adapter
 
 Optional: Used a DialogFragment for editing and adding items
 
 Optional: Add support for selecting the priority of each todo item
 
 Optional: Added date picker for due dates and sorted list based on due dates and display in listview item
 
 Optional: Added Facebook login and logout using facebook-android SDK
 
 Optional: Added Add icon to action bar and changed background color of Main activity
 
 Optional: Saving todo items to mongodb using Restful Express api build on node server. 
 
 Optional: Registering the application with GCM servers asynchronously and storing the deviceId to mongodb.

 Optional: Node cron job to send gcm message about the todo item due for the current date.
 
 Optional: GcmBroadcastReceiver to listen for messages from GCM and display it as a notification on the device.
 
Walkthrough of all user stories:


![alt tag](https://github.com/srivats666/MyTodoApp/blob/master/todoApp.gif)