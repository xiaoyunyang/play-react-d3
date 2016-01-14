var Vis = {};

/****************** Parent - Render ******************/
var BookmarkList = React.createClass({
    loadBookmarksFromServer: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            cache: false,
            success: function(data) {
                this.setState({
                    data: data,
                    items: data.items,
                    tagToItems: data.tagToItems,
                    itemToTags: data.itemToTags,
                    dataset: _.map(data.tagToItems, function(d, i) {
                        return {"key":i,"value":_.size(d.keys),"tag":""+d.tag};
                    })
                });
                this.setState({ //requires separate because it takes time for the state vars to update
                    initialDataset: this.state.dataset,
                    savedDataset: this.state.dataset,
                    initialItems: this.state.items,
                    initialTagToItems: this.state.tagToItems,
                    initialItemToTags: this.state.itemToTags
                });
                this.drawInteractiveChart();
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    filterListByName: function(event){
        var updatedItems = this.state.taggedItems;
        updatedItems = updatedItems.filter(function(item){
            return item.title.toLowerCase().search(
                    event.target.value.toLowerCase()) !== -1;
        });
        this.setState({items: updatedItems});
    },
    filterListByTag: function(event){
        var updatedItems = this.state.initialItems;
        var updatedTagToItems = this.state.initialTagToItems;

        //Check input
        var inputTag = d3.selectAll("#selectedTag").attr("value").toLowerCase();

        if(inputTag===undefined || inputTag==="") {
            updatedItems = this.state.initialItems;
        } else {
            var withTag = _.findWhere(updatedTagToItems, {tag: inputTag});

            var withTagKey;
            if(!!withTag) {withTagKey = withTag.keys;}

            updatedItems = _.filter(updatedItems, function(d) {
                return _.contains(withTagKey, d.key);
            });
        }


        this.setState({items: updatedItems});
        this.setState({taggedItems: updatedItems});

        this.drawNonInteractiveChart(inputTag);
    },
    drawInteractiveChart: function() {

        w = this.state.windowWidth;
        h = 300;
        padding = 30;
        barPadding = 25;
        var svg = d3.select("#myChart").attr("width", w).attr("height", h);
        svg.selectAll("rect").remove();
        svg.selectAll("g").remove();
        var xScale = d3.scale.ordinal()
            .domain(this.state.dataset.map(function(d) { return d.tag; }))
            .rangeRoundBands([padding, w-padding/2], 0.05);
        var yScale = d3.scale.linear()
            .domain([0, d3.max(this.state.dataset, value)])
            .range([padding, h-padding]); //reverse because (0,0) is at top left corner
        var xAxis = d3.svg.axis()
            .scale(xScale)
            .orient("top");
        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("right")
            .ticks(10);

        //Create bars
        bars = svg.selectAll("rect").data(this.state.dataset, key);
        bars.enter()
            .append("rect")
            .attr("x", function(d, i){
                return xScale(d.tag);
            })
            .attr("y", function(d) {
                return padding;
                //return yScale(d);
            })
            .attr("cursor", "pointer")
            .attr({
                width: xScale.rangeBand(),
                height: function(d) { return yScale(d.value); },
                fill: function(d) { return "rgb(0,0,"+(355-(d.value*5))+")"; }
            });

        //Create x-axis
        svg.append("g")
            .attr("class", "x axis")
            .attr("font-size", "18px")
            .attr("transform", "translate(0,"+(padding)+")") //this pushes the line to bottom
            .call(xAxis);

        /***************** Chart Events *****************/
            //interaction with labels

        svg.selectAll("g").selectAll("text")
            .on("click", function(d, i) {
                this.setState({dataset: this.state.savedDataset});

                //clicking any tag label for the chart lets you change the tag
                var selectedTag = d;
                d3.selectAll("#modalTitle").selectAll("h4").remove();
                d3.selectAll("#modalTitle").append("h4").text("you selected: @"+selectedTag);
                d3.selectAll("#selectedTag").attr("value", selectedTag);

                //update the dataset
                var withTag = _.findWhere(this.state.dataset, {tag: selectedTag})
                var updatedDataset = _.without(this.state.dataset, withTag);
                this.setState({dataset: updatedDataset});

                $('#modalTag').openModal();
            }.bind(this) )
            .on("mouseover", function(d) {
                d3.select(this)
                    .attr("fill", "red")
                    .attr("font-size", "24px");

            })
            .on("mouseout", function(d) {
                d3.select(this)
                    .transition()
                    .duration(100)
                    .attr("fill", "black")
                    .attr("font-size", "18px");
            });
        //interaction with rect
        svg.selectAll("rect")
            .on("mouseover", function(d, i) {
                //Get this bar's x/y values, then augment for the tooltip
                var xPosition = parseFloat(d3.select(this).attr("x")) + xScale.rangeBand() / 2;
                var yPosition = parseFloat(d3.select(this).attr("y")) + 14;

                //Create the tooltip label
                svg.append("text")
                    .attr("id", "tooltip")
                    .attr("x", xPosition)
                    .attr("y", yPosition+barPadding/2)
                    .attr("text-anchor", "middle")
                    .attr("font-family", "sans-serif")
                    .attr("font-size", "18px")
                    .attr("font-weight", "bold")
                    .attr("fill", "white")
                    .text(d.value);

                d3.select(this)
                    .attr("fill", "orange");
                d3.selectAll("g")
                    .selectAll("text")
                    .each(function(dTag) {
                        if(dTag==d.tag) {
                            d3.select(this).attr("fill", "orange");
                            d3.select(this).attr("font-size", "24px");
                        }
                    });
            })
            .on("mouseout", function(d) {

                //Remove the tooltip
                d3.select("#tooltip").remove();

                d3.select(this)
                    .transition()
                    .duration(250)
                    .attr("fill", "rgb(0,0,"+(355-(d.value*5))+")");
                d3.selectAll("g")
                    .selectAll("text")
                    .each(function(dTag) {
                        if(dTag==d.tag) {
                            d3.select(this).attr("fill", "black");
                            d3.select(this).attr("font-size", "18px");
                        }
                    });
            })
            .on("click", function(d, i) {
                this.setState({dataset: this.state.savedDataset});
                d3.selectAll("#modalTitle").selectAll("h4").remove();
                d3.selectAll("#modalTitle").append("h4").text("@"+d.tag);
                d3.selectAll("#selectedTag").attr("value", d.tag);
                //add this to prevent axis label overlapping while the old on mouseover is still in transition
                d3.selectAll("g")
                    .selectAll("text")
                    .each(function(dTag) {
                        if(dTag==d.tag) {
                            d3.select(this).attr("fill", "black");
                            d3.select(this).attr("font-size", "18px");
                        }
                    });
                this.filterListByTag();
                $('#modal1').openModal();
                $('ul.tabs').tabs();
            }.bind(this) );


        d3.selectAll("#sortBars")
            .on("click", function() {
                this.setState({dataset: this.state.savedDataset});

                //Flip value of sortOrder
                sortOrder = !sortOrder;
                var x0 = xScale.domain(this.state.dataset.sort(sortOrder
                        ? function(a, b) { return d3.ascending(a.value, b.value); }
                        : function(a, b) { return d3.descending(a.value, b.value); })
                    .map(function(d) { return d.tag; }))
                    .copy();

                svg.selectAll("rect")
                    .sort(function(a, b) { return x0(a.tag) - x0(b.tag); });

                var transition = svg.transition().duration(750),
                    delay = function(d, i) { return i * 50; };

                transition.selectAll("rect")
                    .delay(delay)
                    .attr("x", function(d) { return x0(d.tag); });

                transition.selectAll("text")
                    //.delay(delay)
                    .attr("x", function(d) {
                        return x0(d.tag) + xScale.rangeBand()/2;
                    });
                transition.select(".x.axis")
                    .call(xAxis)
                    .selectAll("g")
                    .delay(delay);

            }.bind(this));
    },
    removeTagFromList: function(event) {
        this.setState({savedDataset: this.state.dataset});
        var inputTag = d3.selectAll("#selectedTag").attr("value").toLowerCase();
        if(inputTag===undefined || inputTag==="") {
        }else {
            this.drawInteractiveChart();
        }
    },
    showAll: function(event){
        w = this.state.windowWidth-55;
        h = 300;
        h = 20;
        d3.select("#myChartModal").attr("width", w).attr("height", h);
        var updatedItems = this.state.initialItems;
        this.setState({items: updatedItems});
        this.setState({taggedItems: updatedItems});
        d3.selectAll("#modalTitle").selectAll("h4").remove();
        d3.selectAll("#modalTitle").append("h4").text("All Bookmarks");
        svg = d3.select("#myChartModal");
        svg.selectAll("rect").remove();
        svg.selectAll("g").remove();
        $('#modal1').openModal();
        $('ul.tabs').tabs();
    },
    print: function(event) {
        var printContents = document.getElementById("printableArea").innerHTML;
        var originalContents = document.getElementById("sheets").innerHTML;
        document.body.innerHTML = printContents;
        d3.selectAll("#demoInfo")
            .append("h2").text("Thank you for supporting looseleaf.us! Please visit looseleaf.us website and leave a feedback on the demo page!")
            .attr("fill", "teal")
            .attr("font-size", "24px");

        window.print();
        document.body.innerHTML = originalContents;
    },
    drawNonInteractiveChart: function(inputTag) {
        /********* non-interactive modal Chart ***************/
        w = this.state.windowWidth-55;
        h = 300;
        padding = 30;
        barPadding = 25;
        svg = d3.select("#myChartModal").attr("width", w).attr("height", h);
        var xScale = d3.scale.ordinal()
            .domain(this.state.dataset.map(function(d) { return d.tag; }))
            .rangeRoundBands([padding, w-padding/2], 0.05);
        var yScale = d3.scale.linear()
            .domain([0, d3.max(this.state.dataset, value)])
            .range([padding, h-padding]); //reverse because (0,0) is at top left corner
        var xAxis = d3.svg.axis()
            .scale(xScale)
            .orient("top");
        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("right")
            .ticks(10);

        /********* Create default scaled ordinal plots ************/
            //remove existing bars and labels if there are any.
        svg.selectAll("rect").remove();
        svg.selectAll("g").remove();
        //Create bars
        bars = svg.selectAll("rect").data(this.state.dataset, key);
        bars.enter()
            .append("rect")
            .attr("x", function(d, i){
                return xScale(d.tag);
            })
            .attr("y", function(d) {
                return padding;
                //return yScale(d);
            })
            .attr({
                width: xScale.rangeBand(),
                height: function(d) { return yScale(d.value); },
                fill: function(d) {
                    if(d.tag==inputTag) {
                        return "orange";
                    }else
                        return "rgb(0,0,"+(355-(d.value*5))+")";
                }
            });

        //Create x-axis
        svg.append("g")
            .attr("class", "x axis")
            .attr("font-size", "18px")
            .attr("transform", "translate(0,"+(padding)+")") //this pushes the line to bottom
            .call(xAxis);
    },
    getInitialState: function() {
        return {
            data: [],
            windowWidth: window.innerWidth,

            initDataset: [],
            dataset: [],
            savedDataset: [],

            // item information, including name, short description, url to the actual item,
            // favicon, (maybe eventually votes?)
            initialItems: [],
            taggedItems: [],
            items: [],

            //mapping from each item to a set of tags associated with the item
            initialTagToItems: [],
            tagToItems: [],

            //mapping from each tag to a set of items which contains the tag
            initialItemToTags: [],
            itemToTags: []
        };
    },
    handleResize: function(e) {
        this.setState({windowWidth: window.innerWidth});
    },
    componentDidMount: function() {
        this.loadBookmarksFromServer();
        window.addEventListener('resize', this.handleResize);

        //the following code sends GET Ajax calls to server every pollInterval
        // setInterval(this.loadBookmarksFromServer, this.props.pollInterval);
    },
    render: function() {
        return (

            <div className="filter-list">
                <span className="center">
                    <p>ChartMenu options:
                        <a id="sortBars" className="waves-effect waves-light btn modal-trigger modal-close">Sort Bars</a>
                        <a id="showAll" className="waves-effect waves-light btn modal-trigger modal-close" href="#modal1" onClick={this.showAll}>Show All</a>
                    </p>
                </span>
                <svg id="myChart" className="center-svg"></svg>
                <div id="modalTag" className="modal">
                    <div className="modal-content">
                        <div id="modalTitle" value="hr"></div>
                        <p>Would you like to remove the selected tag from your personal ChartMenu?</p>
                    </div>
                    <div className="modal-footer">
                        <a href="#!" className=" modal-action modal-close waves-effect waves-green btn-flat" onClick={this.removeTagFromList}>Remove</a>
                    </div>
                </div>
                <div id="modal1" className="modal bottom-sheet">
                    <header id="header" class="alt">
                        <div id="modalTitle" value="hr"></div>
                        <nav id="nav">
                            <ul>
                                <li><a href="" className="modal-action modal-close" onClick={this.print}>Print</a></li>
                                <li><a href="#!" className="modal-action modal-close">Close</a>
                                </li>
                            </ul>
                        </nav>
                    </header>
                    <div id="printableArea" className="modal-content">
                        <svg id="myChartModal" className="center-svg"></svg>
                        <input type="text" placeholder="Search By Name" onChange={this.filterListByName}/>
                        <div id="selectedTag" type="text" value="foo"></div>
                        <div className="row-mat">
                            <div className="col s12">
                                <ul className="tabs">
                                    <li className="tab col s3"><a class="active" href="#listView">List View</a></li>
                                    <li className="tab col s3"><a href="#gridView">Grid View</a></li>
                                </ul>
                            </div>
                            <div id="listView" className="col s12">
                                <List items={this.state.items} itemToTags={this.state.itemToTags}/>
                            </div>
                            <div id="gridView" className="col s12">
                                <Grid items={this.state.items} itemToTags={this.state.itemToTags}/>
                            </div>
                        </div>
                        <div id="demoInfo"></div>
                    </div>
                </div>
            </div>
        );
    }
});

/****************** Component List ******************/
var List = React.createClass({
    render: function() {
        var itemTags = this.props.itemToTags;
        return (
            <ul className="collection"> {
                this.props.items.map(function(item) {
                    var objTags = _.findWhere(itemTags, {key: item.key}).tags;
                    return (
                        <li className="collection-item avatar" key={item.key}>
                            <img className="circle" src={item.favicon}/>
                            <span className="title"><a target="_blank" href={item.url}>{item.title}</a></span>
                            <p>{item.description}</p>
                            <p>Collected by {item.username}</p>
                            <div className="tagcloud"> {
                                objTags.map(function(t) {
                                    return <a><span>{t}</span></a>
                                })
                            } </div>
                            <a href="#!" className="secondary-content"><i className="material-icons">grade</i></a>
                        </li>
                    );
                })
            } </ul>
        );
    }
});

/****************** Component Grid ******************/
var Grid = React.createClass({
    render: function(){
        var itemTags = this.props.itemToTags;
        return (
            <div className="row-mat"> {
                this.props.items.map(function(item) {
                    return (
                        <div className="col s3">
                            <div className="card">
                                <div id={item.key} className="card-image waves-effect waves-block waves-light" >
                                    <img className="activator" src={item.favicon}/>
                                </div>
                                <div className="card-action">
                                    <a target="_blank" href={item.url}>Visit Site</a>
                                </div>
                                <div className="card-reveal">
                        <span className="card-title grey-text text-darken-4">{item.title}<i className="material-icons right">close</i>
                        </span>
                                    <p>{item.description}</p>
                                </div>
                            </div>
                        </div>
                    );
                })
            }</div>
        )
    }
});

var Bookmark = React.createClass({
    render: function() {
        var rawMarkup = marked(this.props.children.toString(), {sanitize: true});

        return (
            <div className="comment">
                <h2 className="commentAuthor">
                    {this.props.username}
                </h2>
                <span dangerouslySetInnerHTML={{__html: rawMarkup}} />
            </div>
        );
    }
});


var CommentForm = React.createClass({
    handleSubmit: function(e) {
        e.preventDefault();
        var author = React.findDOMNode(this.refs.author).value.trim();
        var text = React.findDOMNode(this.refs.text).value.trim();
        if (!text || !author) {
            return;
        }

        var commentUrl = "http://localhost:9000/comment?author=" + author + "&text=" + text;
        $.ajax({
            url: commentUrl,
            method: 'POST',
            dataType: 'json',
            cache: false,
            success: function(data) {
                console.log(data)
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(commentUrl, status, err.toString());
            }.bind(this)
        });

        // clears the form fields
        React.findDOMNode(this.refs.author).value = '';
        React.findDOMNode(this.refs.text).value = '';
        return;
    },
    render: function() {
        return (
            <form className="commentForm" onSubmit={this.handleSubmit}>
                <input type="text" placeholder="Your name" ref="author" />
                <input type="text" placeholder="Say something..." ref="text" />
                <input className="btn btn-primary" type="submit" value="Post" />
            </form>
        );
    }
});



React.render(
    <BookmarkList url="http://localhost:9000/react/bookmarks" pollInterval={100000} />,
    document.getElementById('content')
);