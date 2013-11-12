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

$('form.home-search input[name=babyname]').typeahead({
  name: 'names',
  remote: {
    url: "//localhost:33333/data/names-typeahead.json"
  },
  template: _.template('<div class="name-result"><div class="row"> <div class="col-sm-6"><p class="name"><%= value %></p></div> <div class="col-sm-1"><p class="gender"><i class="fa fa-male"></i></p></div> <div class="col-sm-5"> SPARKLINE </div></div></div>'),
  local: ['alpha','allpha2','alpha3','bravo','charlie','delta','epsilon','gamma','zulu']
});

$('.tt-query').css('background-color','#fff')


//<div class="row"> <div class="col-sm-1"> <i class="fa fa-male"></i> </div> <div class="col-sm-5"> <p class="name"><%= value %></p> </div> <div class="col-sm-6"> SPARKLINE </div> </div>
