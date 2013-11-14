var width = $(".map").width();
var height = transformHeight(width);
var scale = transformScale(width);

var svg = d3.select('.map').insert('svg')
    .attr('width', width)
    .attr('height', height);
var group = svg.append('g').attr("id", "states");

var stateSelected;

var projection = d3.geo.albersUsa();
var path = d3.geo.path().projection(projection);
group.attr('transform', 'scale('+scale+')');

var color = d3.scale.linear().domain([1,12]).range(['red', 'blue']);

function transformScale(width) {
  return width/940;
}
function transformHeight(width) {
  return (width/940)*500;
}

function resetMap(targetWidth) {
  width = targetWidth;
  height = transformHeight(targetWidth);
  scale = transformScale(targetWidth);
  group.transition()
    .duration(500)
    .attr("transform", "scale(" + scale + ")");
  svg.transition()
    .duration(500)
    .attr('width', width)
    .attr('height', height);
  if (stateSelected) {
    var d = stateSelected;
    stateSelected = null;
    selectState(d);
  }
}

function selectState(d) {
  var x = 0,
      y = 0,
      k = scale;

  if (d && stateSelected !== d) {
    var centroid = path.centroid(d);
    x = -centroid[0] + width/4;
    y = -centroid[1] + height/4;
    k = 2;
    stateSelected = d;
  } else {
    stateSelected = null;
  }

  group.selectAll("path")
    .classed("active", stateSelected && function(d) { return d === stateSelected; });

  group.transition()
    .duration(1000)
    .attr("transform", "scale(" + k + ") translate(" + x + "," + y + ")")
    .style("stroke-width", 1.5 / k + "px");
}

d3.json('/data/us-states.json', function(collection) {
  group.selectAll('path')
    .data(collection.features)
    .enter().append('svg:path')
    .attr('id', function(d){return d.properties.name.replace(/\s+/g, '')})
    .attr("class", function(d) { return "state " + d.properties.code})
    .attr('d', d3.geo.path().projection(projection))
    .style('stroke', 'white')
    .style('stroke-width', 1)
    .on('click', selectState);

  group.selectAll("text")
    .data(collection.features)
    .enter().append("svg:text")
    .text("")
    .attr("x", function(d){
        return path.centroid(d)[0];
    })
    .attr("y", function(d){
        return  path.centroid(d)[1];
    })
    .attr("text-anchor","middle")
    .attr('font-size', function (d) {
      var area = path.area(d);
      var size = (area/1000) * 3;
      size = Math.min(size, 11);
      size = Math.max(size, 6);
      size = Math.round(size);
      return size+"pt";
    });

  d3.json('/data/names.json', function(collection) {
    group.selectAll('text')
      .data(collection)
      .text(function(d){
          return d.popularName;
      })
  });
});


$(window).on("resize", function() {
  var targetWidth = $(".map").width();
  if (targetWidth != width) {
    resetMap(targetWidth);
  }
});

$(".exclusive-checkboxes").each(function() {
  var $checkboxes = $(this).find("input:checkbox").change(function () {
    if ($(this).is(":checked")) {
      $checkboxes.not(this).each(function() {
        $(this).prop('checked', false);
        $(this).parent().removeClass('active');
      });
    }
  });
});


/* TYPEAHEAD */
var spinnerOpts = {
  lines: 13, // The number of lines to draw
  length: 6, // The length of each line
  width: 2, // The line thickness
  radius: 6, // The radius of the inner circle
  corners: 1, // Corner roundness (0..1)
  rotate: 0, // The rotation offset
  direction: 1, // 1: clockwise, -1: counterclockwise
  color: '#000', // #rgb or #rrggbb or array of colors
  speed: 2, // Rounds per second
  trail: 60, // Afterglow percentage
  shadow: false, // Whether to render a shadow
  hwaccel: false, // Whether to use hardware acceleration
  className: 'spinner', // The CSS class to assign to the spinner
  zIndex: 2e9, // The z-index (defaults to 2000000000)
  top: 'auto', // Top position relative to parent in px
  left: 0,
  right: 10 // Left position relative to parent in px
};

$('form.home-search input[name=babyname]').each(function() {
  var $search = $(this);
  var $form = $search.parents('form');
  var $gender = $form.find('input[name=gender]').change(function() {
    var val = $search.val();
    $search.typeahead('setQuery', val).focus();
  });
  var spinner;


  $search.typeahead({
    name: 'names',
    valueKey: "name",
    limit: 20,
    remote: {
      url: "/api/v1/names?limit=20&q=<%= query %><%= gender %>",
      cache: true,
      beforeSend: function() {
        spinnerOpts.left = $search.width() - 10;
        spinner = new Spinner(spinnerOpts).spin($search.parent().get(0));
      },
      replace: function (url, urlEncodedQuery) {
        var template = _.template(url);
        var gender = $form.find('input[name=gender]:checked').val();
        gender = (typeof gender !== "undefined") ? "&gender="+gender : "";
        url = template({gender: gender, query: urlEncodedQuery});
        return url;
      },
      filter: function (parsedResponse) {
        if (spinner) {
          spinner.stop();
          spinner = null;
        }
        return parsedResponse.response;
      }
    },
    template: _.template('<div class="name-result"><div class="row"> <div class="col-sm-6"><p class="name"><i class="fa fa-<% if (gender === "M") {print("male");} else {print("female");} %>"></i> <%= name %></p></div></div></div>'),
  });
});

$('.tt-query').css('background-color','#fff')


//<div class="row"> <div class="col-sm-1"> <i class="fa fa-male"></i> </div> <div class="col-sm-5"> <p class="name"><%= value %></p> </div> <div class="col-sm-6"> SPARKLINE </div> </div>
