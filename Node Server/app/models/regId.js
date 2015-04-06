/**
 * Created by sjayaram on 4/2/2015.
 */
// load the things we need
var mongoose = require('mongoose');

// define the schema for our user model
var regIdSchema = mongoose.Schema({
    deviceRegId        : String,
    userId     : String
});

// create the model for users and expose it to our app
module.exports = mongoose.model('RegId', regIdSchema);