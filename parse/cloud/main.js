Parse.initialize("nmqzdi7YSCSGPBixx11Mjvu6tF8mpzTZ9yObsM1", 
    "xeJa7O6dzR8oulCfD31cmUwI3frlngCFSY1jSEr8");

Parse.Push.send({
    channels: [ "Giants", "Mets" ],
    data: {
        alert: "The Giants won against the Mets 2-3."
    }
}, {
    success: function() {
        // Push was successful
    },
    error: function(error) {
        // Handle error
    }
});
