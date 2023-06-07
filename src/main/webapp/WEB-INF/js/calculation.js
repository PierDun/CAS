async function sendExpression () {
    let expression = $("#expression").val();
    expression = expression.replace(/\s/g,'');

    if (check(expression)) {
        const payload = createArg(expression);

        //$("#result").html(katex.renderToString(createFunction(payload)));

        const json = JSON.stringify(
            payload
        );

        let response = await fetch('/api/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: json
        });

        if (response.ok) {
            $("#error").text("");
            let json = await response.json();

            let table = "\\begin{array}{ | c | c | } \\hline \\ ";
            table += "Number &" +  "Evaluation " + " \\\\ " + " \\hline \\ ";
            let j = 1;
            for (let i = 0; i < json.length; i = i + 2) {
                table += " \\\\ ";
                table += j++ + " & ";
                table += createFunction(json[i]) + " = " + createFunction(json[i + 1]);
                table += " \\\\ ";
                table += " \\\\ \\hline \\ ";
            }
            table += " \\\\ \\end {array}";
            $("#result").html(katex.renderToString(table));
            $("#tree").html("");
            drawTree(json[json.length - 1]);
        } else {
            let error = await response.text();
            $("#error").text(error);
        }
    } else {
        $("#error").text("Introduced expression is not correct!");
    }
}


function check (string) {
    let args = Array.from(string);
    let counter = 0;

    for (let j = 0; j < args.length; j++) {
        if (args[j] === '[')
            counter++;
        if (args[j] === ']')
            counter--;
    }

    return counter === 0;
}

function createArg (expression) {
    if (expression.indexOf('[') !== -1) {
        const head = expression.substring(0, expression.indexOf('['));
        let string = expression.substring(expression.indexOf('[') + 1, expression.lastIndexOf(']'));
        let arguments = [];

        let args = Array.from(string);
        let commas = [0];
        let counter = 0;

        for (let j = 0; j < args.length; j++) {
            if (args[j] === '[')
                counter++;
            if (args[j] === ']')
                counter--;
            if (args[j] === ',' && counter === 0) {
                commas.push(j);
            }
        }

        commas.push(string.length);

        if (commas.length > 2) {
            let j = 1;
            arguments[0] = createArg(string.substring(commas[0], commas[1]));
            for (let i = 1; i < commas.length - 1; i++) {
                arguments[j] = createArg(string.substring(commas[i] + 1, commas[i + 1]));
                j++;
            }
        } else {
            arguments[0] = createArg(string);
        }

        return {
            head: head,
            arguments: arguments
        };
    } else {
        return {
            head: expression
        };
    }
}

function createFunction (expression) {
    let result = ''
    switch (expression.head) {
        case 'PLUS':
            for (let i = 0; i < expression.arguments.length; i++) {
                result += "{" + createFunction(expression.arguments[i]) + "} + ";
            }
            result = result.substring(0, result.length - 3);
            break;

        case 'MINUS':
            for (let i = 0; i < expression.arguments.length; i++) {
                result += "{" + createFunction(expression.arguments[i]) + "} - ";
            }
            result = result.substring(0, result.length - 3);
            break;

        case 'MULTIPLY':
            for (let i = 0; i < expression.arguments.length; i++) {
                if (expression.arguments[i].head === "PLUS" || expression.arguments[i].head === "MINUS") {
                    result += "{ (" + createFunction(expression.arguments[i]) + ") } * ";
                } else
                    result += "{" + createFunction(expression.arguments[i]) + "} * ";
            }
            result = result.substring(0, result.length - 3);
            break;

        case 'DIVIDE':
            result += "\\small\\frac{" + createFunction(expression.arguments[0]) + "}{" + createFunction(expression.arguments[1]) + "}";
            break;

        case 'SIN':
            result += "\\sin{(" + createFunction(expression.arguments[0]) + ")}";
            break;

        case 'COS':
            result += "\\cos{ (" + createFunction(expression.arguments[0]) + ") }";
            break;

        case 'TAN':
            result += "\\tan{ (" + createFunction(expression.arguments[0]) + ") }";
            break;

        case 'CTG':
            result += "\\ctg{ (" + createFunction(expression.arguments[0]) + ") }";
            break;

        case 'LOG':
            result += "\\log_{" + createFunction(expression.arguments[1]) + "} { (" + createFunction(expression.arguments[0]) + ") }";
            break;

        case 'LN':
            result += "\\ln{ (" + createFunction(expression.arguments[0]) + ") }";
            break;

        case 'POWER':
            if (expression.arguments[0].head === 'PLUS' || expression.arguments[0].head === 'MINUS' ||
                expression.arguments[0].head === 'MULTIPLY') {
                result += "( " + createFunction(expression.arguments[0]) + " )";
            } else
                result += createFunction(expression.arguments[0]);
            result += "^{" + createFunction(expression.arguments[1]) + "}";
            break;

        case 'DIF':
            result += "( " + createFunction(expression.arguments[0]) + " )'";
            break;

        default:
            result += expression.head;
            if (expression.arguments.length > 1) {
                result += " ["
                for (let i = 0; i < expression.arguments.length; i++) {
                    result += createFunction(expression.arguments[i]) + ", "
                }
                result = result.substring(0, result.length - 2);
                result += " ]"
            }
    }
   return result;
}

function createTree (expression) {
    const name = expression.head;
    let children = [];

    if (expression.arguments.length > 0) {
        for (let i = 0; i < expression.arguments.length; i++) {
            children[i] = createTree(expression.arguments[i]);
        }

        return {
            name: name,
            children: children
        }
    }

    return {
        name: name
    }
}

function drawTree (expression) {
    const margin = {
            top: 20,
            right: 120,
            bottom: 20,
            left: 120
        },
        width = 960 - margin.right - margin.left,
        height = 800 - margin.top - margin.bottom;

    let i = 0,
        duration = 750,
        rectW = 60,
        rectH = 30;

    const root = createTree(expression);

    let zm;
    const svg = d3.select("#tree").append("svg").attr("width", 700).attr("height", 1000)
        .call(zm = d3.behavior.zoom().scaleExtent([1, 3]).on("zoom", redraw)).append("g")
        .attr("transform", "translate(" + 350 + "," + 20 + ")");

    zm.translate([350, 20]);

    root.x0 = 0;
    root.y0 = height / 2;

    function collapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(collapse);
            d.children = null;
        }
    }

    root.children.forEach(collapse);

    const tree = d3.layout.tree().nodeSize([70, 40]);

    const diagonal = d3.svg.diagonal()
        .projection(function (d) {
            return [d.x + 60 / 2, d.y + 30 / 2];
        });

    update(root);

    function update(source) {
        const nodes = tree.nodes(root).reverse(),
            links = tree.links(nodes);

        nodes.forEach(function(d) { d.y = d.depth * 80; });

        const node = svg.selectAll("g.node")
            .data(nodes, function (d) {
                return d.id || (d.id = ++i);
            });

        const nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            })
            .on("click", click);

        nodeEnter.append("rect")
            .attr("width", 60)
            .attr("height", 30)
            .attr("stroke", "black")
            .attr("stroke-width", 1)
            .style("fill", function (d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        nodeEnter.append("text")
            .attr("x", 60 / 2)
            .attr("y", 30 / 2)
            .attr("dy", ".35em")
            .attr("text-anchor", "middle")
            .text(function (d) {
                return d.name;
            });

        var nodeUpdate = node.transition()
            .duration(duration)
            .attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            });

        nodeUpdate.select("rect")
            .attr("width", 60)
            .attr("height", 30)
            .attr("stroke", "black")
            .attr("stroke-width", 1)
            .style("fill", function (d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        nodeUpdate.select("text")
            .style("fill-opacity", 1);

        var nodeExit = node.exit().transition()
            .duration(duration)
            .attr("transform", function (d) {
                return "translate(" + source.x + "," + source.y + ")";
            })
            .remove();

        nodeExit.select("rect")
            .attr("width", rectW)
            .attr("height", rectH)
            //.attr("width", bbox.getBBox().width)""
            //.attr("height", bbox.getBBox().height)
            .attr("stroke", "black")
            .attr("stroke-width", 1);

        nodeExit.select("text");

        const link = svg.selectAll("path.link")
            .data(links, function (d) {
                return d.target.id;
            });

        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("x", 60 / 2)
            .attr("y", 30 / 2)
            .attr("d", diagonal);

        link.transition()
            .duration(duration)
            .attr("d", diagonal);

        link.exit().transition()
            .duration(duration)
            .attr("d", function (d) {
                const o = {
                    x: source.x,
                    y: source.y
                };
                return diagonal({
                    source: o,
                    target: o
                });
            })
            .remove();

        nodes.forEach(function (d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
    }

    function click(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }
        update(d);
    }

    function redraw() {
        svg.attr("transform",
            "translate(" + d3.event.translate + ")"
            + " scale(" + d3.event.scale + ")");
    }
}