const mongoose = require('mongoose');
const express = require('express');
const crypto = require('crypto');
const bodyParser = require('body-parser');
const cloudrail = require('cloudrail-si');
cloudrail.Settings.setKey(“[CloudRailKey]”);
const app = express();
const userSchema = new mongoose.Schema({
    uid: String,
    status: String,
    token: String
});
const User = mongoose.model("User", userSchema);

const MONGODB_ADDRESS = 'mongodb://localhost/login-with-sample';

// Connect to DB
mongoose.connection.on("error", function (err) {
    console.error("Mongo DB connection error: " + err);
});
mongoose.connection.on("connected", function () {
    console.log("Successfully connected to Mongo DB");
});
mongoose.connect(MONGODB_ADDRESS, function (err) {
    if (err) {
        console.error("Mongo DB connection error: " + err);
    }
});

app.use(bodyParser.json());

/**
 * When the user has performed the authentication in the frontend part, the
 * obtained access token will be send to the backend and received in this method.
 * This method will instantiate the correct service instance and retrieves the
 * user information from the chosen service (including the user identifier).
 * This identifier will be searched in the database. If there is no entry in the
 * database it will create a new user entry. After that it returns a unique token
 * that can be used by the frontend to do further requests to this service.
 */
app.post("/user/authenticate", (req, res) => {

    /* ----- Receive the user identifier ----- */

    let profile = new cloudrail.services[req.body.name](null, "[TwitterClientID]", "[TwitterClientSecret]");
    let state = req.body.state;
    profile.loadAsString(state);
    profile.getIdentifier((err, identifier) => {
        addUserToDB(identifier, res);
    });
});

function addUserToDB(identifier, res) {
    User.findOne({"uid": identifier}, (err, user) => {
        if(!user) {
            user = new User({
                "uid": identifier,
                "status": "Hello World!",
                "token": crypto.randomBytes(16).toString("hex") // There are better solutions for production
            });
        }

        returnToken(user, res);
    });
}

function returnToken(user, res) {
    user.save((err) => {
        res.set("Token", user.token);
        res.end();
    });
}

/**
 * Return the status of a user. Authorization is required for this request.
 */
app.get("/user/status", (req, res) => {
    let auth = req.get("Authorization").split(" ");

    User.findOne({token: auth[1]}, (err, user) => {
        if(!user) {
            res.status(401).send('Unauthorized');
        } else {
            res.json({status: user.status});
        }
    });
});

/**
 * Sets the status of a user. Authorization is required for this request.
 */
app.post("/user/status", (req, res) => {
    let auth = req.get("Authorization").split(" ");

    User.findOne({token: auth[1]}, (err, user) => {
        if(!user) {
            res.status(401).send('Unauthorized');
        } else {
            user.status = req.body.status;
            user.save((err) => {
                res.end();
            });
        }
    });
});

const port = process.env.PORT || 5000;
app.listen(port, function() {
    console.log("Listening on " + port);
});
