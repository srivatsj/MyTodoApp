var Todo = require('../app/models/todo');
var RegId = require('../app/models/regId');

module.exports = function(app) {

    app.get('/api/todos/:userId', function (req, res) {
        Todo.find({ userId: req.params.userId }).sort({dueDate: 1}).exec(function(err, todos) {
            if (!err) {
                return res.send(todos);
            } else {
                return console.log(err);
            }
        });
    });

    app.post('/api/todos', function (req, res){
        var todo;
        console.log("POST: ");
        console.log(req.body);
        todo = new Todo();
        todo.name = req.body.name;
        todo.priority = req.body.priority;
        todo.dueDate= new Date(req.body.dueDate);
        todo.userId = req.body.userId;
        todo.createDate= new Date();

        todo.save(function (err) {
            if (!err) {
                return console.log("created");
            } else {
                return console.log(err);
            }
        });

        return res.send(todo);
    });

    app.put('/api/todos/:id', function (req, res){
        var todo;
        console.log("PUT: ");
        console.log(req.body);

        Todo.findById(req.params.id, function(err, todo) {
            if (err)
                res.send(err);

            todo.name = req.body.name;
            todo.priority = req.body.priority;
            todo.dueDate= req.body.dueDate;

            // Save the beer and check for errors
            todo.save(function(err) {
                if (!err) {
                    console.log("updated");
                } else {
                    console.log(err);
                }
                return res.send(todo);
            });
        });
    });

    app.delete('/api/todos/:id', function (req, res){
        console.log("DELETE: ");
        console.log(req.body);

        Todo.findByIdAndRemove(req.params.id, function(err) {
            if (!err) {
                console.log("removed");
                return res.send('');
            } else {
                console.log(err);
            }
        });
    });

    app.post('/api/regId', function (req, res){
        var regId;
        console.log("POST: ");
        console.log(req.body);
        regId = new RegId();
        regId.deviceRegId = req.body.deviceRegId;
        regId.userId = req.body.userId;

        regId.save(function (err) {
            if (!err) {
                return console.log("created");
            } else {
                return console.log(err);
            }
        });
        return res.send(regId);
    });

}