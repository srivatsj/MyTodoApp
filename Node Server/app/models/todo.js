/**
 * Created by sjayaram on 3/16/2015.
 */
// load the things we need
var mongoose = require('mongoose');

// define the schema for our user model
var todoSchema = mongoose.Schema({
        userId        : String,
        name        : String,
        priority     : String,
        dueDate         : Date,
        createDate : Date
});

// create the model for users and expose it to our app
module.exports = mongoose.model('Todo', todoSchema);
