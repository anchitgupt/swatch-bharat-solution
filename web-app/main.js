var type = ""
firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    console.log("Hello");
    // User is signed in.
    $(".login-cover").hide();

    if (type == "garbage") {
      window.location.replace("garbage.html");
    }
    if (type == "pit") {
      window.location.replace("pit.html");
    }
    if (type == "child")
      window.location.replace("child.html");
    if (type == "log")
      window.location.replace("log.html");

  } else {
    $(".login-cover").hide();
  }
});

$("#garbage").click(
  function() {
    console.log("hello");
    type = "garbage";
    var dialog = document.querySelector('#loginDialog');
    if (!dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.showModal();
  }
);

$("#pit").click(
  function() {
    console.log("hello");
    type = "pit";
    var dialog = document.querySelector('#loginDialog');
    if (!dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.showModal();
  }
);

$("#log").click(
  function() {
    console.log("hello");
    type = "log";

    var dialog = document.querySelector('#loginDialog');
    if (!dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.showModal();
  }
);

$("#child").click(
  function() {
    console.log("hello");
    type = "child";

    var dialog = document.querySelector('#loginDialog');
    if (!dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.showModal();
  }
);
/*
SIGN in PROCESSS
*/
$("#loginBtn").click(
  function() {

    console.log("loginBTN");
    var email = $("#loginEmail").val();
    var pass = $("#loginPassword").val();

    if (email != "" && pass != "") {

      $("#loginProgress").show();
      $("#loginBtn").hide();

      firebase.auth().signInWithEmailAndPassword(email, pass).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // ...
        console.log("authenticate");
        $("#loginProgress").hide();
        $("#loginBtn").show();
        $("#loginError").show().text(errorMessage);
        if (errorMessage == "") {
            window.location.replace("garbage.html");

        }
      });
    }
  }
);

/*
SIGN OUT PROCESSS
*/
$("#signOutBtn").click(
  function() {
    firebase.auth().signOut().then(function() {
      window.location.replace("index.html");
      // Sign-out successful.
    }).catch(function(error) {
      // An error happened.
      alert(error.message);
    });
  }
);
