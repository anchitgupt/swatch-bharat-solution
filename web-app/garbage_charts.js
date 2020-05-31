var type = 'garbage';
var i;
var c = [],
  c1 = [];


$('#analysisBtn').click(function() {
  window.location.replace("garbage.html");
});
$('#historyBtn').click(function() {
  window.location.replace("garbage.html");
});
$('#currentBtn').click(function() {
  window.location.replace("garbage.html");
});
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


window.onload = function() {
  //firebase start
  $('#analysisBtn').hide();
  $('#historyBtn').hide();

  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      console.log("Hello");
      dataChart();
    } else { //if uer not signed in
      window.location.replace("index.html");
    } //end else
  }); //firebase end
}

function dataChart() {

  getData();

}

function getData() {
  var keys;
  var h;
  firebase.database().ref(type).child('old').on('value',
    //get data from the firebase
    function(data) {
      datapoints = [];
      h = data.val();
      if (data.val() == null) {
        c = [];
      } else {
        keys = Object.keys(data.val());

        console.log(h);
        console.log(keys);

        var arr = []; //= [h];
        //  console.log("A: " +a);
        var b;

        for (i = 0; i < keys.length; i++) {
          arr.push({
            x: h[keys[i]].time,
            y: 1
          });
        }

        console.log(arr);

        var g = arr;
        arr = g;

        var result = [];

        arr.forEach(function(a) {
          if (!this[a.x]) {
            this[a.x] = {
              x: a.x,
              y: 0
            };
            result.push(this[a.x]);
          }
          this[a.x].y += a.y;
        }, Object.create(null));

        console.log(result);
        console.log(arr);

        c = result;
      }
      firebase.database().ref(type).child('new').on('value',
        //get data from the firebase
        function(data) {
          datapoints = [];
          h = data.val();
          if(data.val() == null){
            c1 = [];
            if(c1 == [] && c == []){alert("No Data To Show");}
            madeChart();
          }
          else{
          keys = Object.keys(data.val());

          console.log(h);
          console.log(keys);

          var arr = []; //= [h];
          //  console.log("A: " +a);
          var b;

          for (i = 0; i < keys.length; i++) {
            arr.push({
              x: h[keys[i]].time,
              y: 1
            });
          }

          console.log(arr);

          var g = arr;
          arr = g;

          var result = [];

          arr.forEach(function(a) {
            if (!this[a.x]) {
              this[a.x] = {
                x: a.x,
                y: 0
              };
              result.push(this[a.x]);
            }
            this[a.x].y += a.y;
          }, Object.create(null));

          console.log(result);
          console.log(arr);

          c1 = result;
          console.log("NEW");
          console.log(c);
          console.log(c1);

          madeChart();
        }
        }
      );
    }
  );



}

function madeChart() {
  console.log(c);
  var pointList = [],
    pointList1 = [];
  console.log(c.length);
  for (i = 0; i < c.length; i++) {
    pointList.push({
      x: new Date(c[i].x.split("/")[2], c[i].x.split("/")[1] - 1, c[i].x.split("/")[0]),
      y: c[i].y
    });
  }

  for (i = 0; i < c1.length; i++) {
    pointList1.push({
      x: new Date(c1[i].x.split("/")[2], c1[i].x.split("/")[1] - 1, c1[i].x.split("/")[0]),
      y: c1[i].y
    });
  }


  console.log("LIST");
  console.log(pointList);
  var chart = new CanvasJS.Chart("chartContainer", {
    animationEnabled: true,
    exportEnabled: true,
    title: {
      text: "Garbage Issues"
    },
    axisX: {
      valueFormatString: "DD MMM,YY"
    },
    axisY: {
      title: "Count",
      includeZero: true,
      suffix: ""
    },
    legend: {
      cursor: "pointer",
      fontSize: 16,
      itemclick: toggleDataSeries
    },
    toolTip: {
      shared: true
    },
    data: [{
      name: "Resolved",
      type: "spline",
      yValueFormatString: "",
      showInLegend: true,
      dataPoints: pointList
    }, {
      name: "UnResolved",
      type: "spline",
      yValueFormatString: "",
      showInLegend: true,
      dataPoints: pointList1
    }]
  });
  chart.render();

  function toggleDataSeries(e) {
    if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
      e.dataSeries.visible = false;
    } else {
      e.dataSeries.visible = true;
    }
    chart.render();
  }
}
