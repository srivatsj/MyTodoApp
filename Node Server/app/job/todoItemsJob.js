/**
 * Created by sjayaram on 4/4/2015.
 */
var Todo  = require('../../app/models/todo');
var RegId = require('../../app/models/regId');
var config = require('../../config/config.js');
var gcm = require('node-gcm');
var HashMap = require('hashmap');
var CronJob = require('cron').CronJob;

module.exports = function(app) {

    //try {
      //  var job = new CronJob('*/5 * * * *', function () {
        //    console.log('You will see this message every 5 minutes');
          //  sendNotifications();
        //}, null, true, 'America/Los_Angeles');
    //}catch(ex) {
      //  console.log("cron pattern not valid");
    //}


    app.get('/api/job', function (req, res) {

        sendNotifications();
        res.send(200);

    });

    var formatDate = function() {
        var d = new Date(),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('-');
    };

    var sendNotifications = function()
    {
        var startDate = new Date(formatDate()); // this is the starting date that looks like ISODate("2014-10-03T04:00:00.188Z")
        startDate.setSeconds(0);
        startDate.setHours(0);
        startDate.setMinutes(0);

        var dateMidnight = new Date(startDate);
        dateMidnight.setHours(23);
        dateMidnight.setMinutes(59);
        dateMidnight.setSeconds(59);
        var message;
        var sender = new gcm.Sender(config.gcmSenderId);

        Todo.find({ dueDate : { "$gte" : startDate, "$lt" : dateMidnight }}).exec(function (err, todos) {
            if (!err)
            {
                getDeviceIds(todos, function(err, map) {
                    if (err) {
                        console.error(err.stack || err);
                    } else {

                        map.forEach(function(value, key) {
                            console.log(key + " : " + value);

                            message = new gcm.Message();
                            message.addData('key1', "Your todo item " + value + " is due today");

                            sender.send(message, key, function (err, result) {
                                if (err) {
                                    console.error(err);
                                }
                                else {
                                    console.log(result);
                                }
                            });

                        });
                    }
                });

            } else {
                return console.error(err);
            }
        });

        console.log('success');

    };

    var getDeviceIds = function(todos, next) {

        var map = new HashMap();

        todos.forEach(function(todo, index) {

            RegId.find({userId: todo.userId}).sort({dueDate: 1}).exec(function (err, regs) {
                if (!err) {

                    regs.forEach(function(reg, index)
                    {
                        map.set(reg.deviceRegId, todo.name);
                        console.log('id is ' + reg.deviceRegId);
                    });

                    return next(null, map);

                } else
                {
                    console.log(err);
                    return next(err);
                }
            });
        });
    };


}