var semanticAnnotation = {
	init: function () {
		this.cacheDom();
		this.bindEvents();
		this.render();
		this.addPlugins();
	},

	cacheDom: function () {
		this.$el = $('#image');
	},

	bindEvents: function () {
		events.on('pagenumberChanged', function (data) {
			this.pageID = data.pageID;
		}.bind(this));
		anno.addHandler('onAnnotationCreated', function (annotation) {
			this.storeAnnotation(annotation);
		}.bind(this));
		anno.addHandler('onAnnotationRemoved', function (annotation) {
			this.removeAnnotation(annotation);
		}.bind(this));
		events.on('folderSelected', this.loadAnnotations.bind(this));
		events.on('viewerOpened', this.showAnnotations.bind(this));
	},
	render: function () {
	},

	removeAnnotation: function (annotation) {
		var request = new XMLHttpRequest();
		var body = JSON.stringify(annotation);
		request.open('DELETE', 'annotation', true);
		request.setRequestHeader('Content-type', 'application/json');
		request.send(body);
		request.onreadystatechange = function () {
			if (this.readyState === XMLHttpRequest.DONE && this.status === 204) {
				console.log('Deleted RDF triples.');
			}
		}
	},

	loadAnnotations: function () {
		this.annotations = [];
		var that = this;
		var request = new XMLHttpRequest();
		request.open('GET', 'annotation', true);
		request.onreadystatechange = function () {
			if (this.readyState == 4 && this.status == 200) {
				var jsonArray = JSON.parse(request.response);
				for (let i in jsonArray) {
					var dim = jsonArray[i].selector.split(',');
					that.annotations.push({
						source: jsonArray[i].source,
						text: jsonArray[i].type,
						verbatim: jsonArray[i].verbatim,
						selector: jsonArray[i].selector,
						shapes: [{
							type: 'rect',
							geometry: {
								x: parseFloat(dim[0].replace(/\D+/, '')),
								y: parseFloat(dim[1]),
								width: parseFloat(dim[2]),
								height: parseFloat(dim[3])
							}
						}],
					});
				}
			}
		}
		request.send();
	},

	showAnnotations: function (page) {
		this.annotations.forEach(function (elem) {
			if (elem.source == page) {
				anno.addAnnotation({
					src: 'dzi://openseadragon/something',
					source: elem.source,
					text: elem.text,
					verbatim: elem.verbatim,
					selector: elem.selector,
					shapes: [{
						type: 'rect',
						geometry: {
							x: elem.shapes[0].geometry.x,
							y: elem.shapes[0].geometry.y,
							width: elem.shapes[0].geometry.width,
							height: elem.shapes[0].geometry.height
						}
					}],
				});
			}
		})
	},

	storeAnnotation: function (annotation) {
		//mandatory annotation info
		annotation.date = new Date().toISOString();  // ISO 8601 (e.g. 2021-01-12T14:23:11.646Z)
		annotation.creator = accounts.person[0].IRI;

		//target
		annotation.source = this.pageID;
		annotation.selector = 'xywh=' + annotation.shapes[0].geometry.x + ',' + annotation.shapes[0].geometry.y + ',' + annotation.shapes[0].geometry.width + ',' + annotation.shapes[0].geometry.height;

		//textual body
		annotation.verbatim = $('#verbatim').val();
		annotation.language = $('#language').val();

		//semantic annotation info
		annotation.type = annotation.text.toLowerCase().trim();
		annotation.property = $('select[name=property]').val();
		annotation.organismID = '';

		if (annotation.property === "hasIdentification") {
			annotation.belongstotaxon = $('#belongstotaxon').val();
			annotation.rank = $('#rank').val();
			annotation.person = $('#identifiedBy').val();
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property === 'additionalIdentification') {
			annotation.organismID = $('#organismID').val();
			annotation.identificationID = $('#identificationID').val();
			annotation.belongstotaxon = $('#belongstotaxon').val();
			annotation.rank = $('#rank').val();
		} else if (annotation.property === 'verbatimEventDate') {
			annotation.year = $('#year').val();
			annotation.month = $('#month').val();
			annotation.day = $('#day').val();
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property === 'measuresOrDescribes') {
			if ($('#type').val() === 'propertyorattribute') {
				annotation.propertyorattribute = $('#propertyorattribute').val();
			} else if ($('#type').val() === "anatomicalentity") {
				annotation.anatomicalentity = $('#anatomicalentity').val();
				annotation.organismID = $('#organismID').val();
			}
		} else if (annotation.property === "basedOn") {
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property === "locatedAt") {
			annotation.geonamesfeature = $('#geonamesfeature').val();
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property == "additionalLocatedAt") {
			annotation.geonamesfeature = $('#geonamesfeature').val();
			annotation.organismID = $('#organismID').val();
			annotation.occurrenceID = $('#occurrenceID').val();
		} else if (annotation.property === 'scientificNameAuthorship') {
			annotation.person = $('#person').val();
			annotation.belongstotaxon = $('#belongstotaxon').val();
		} else if (annotation.property === 'identifiedBy') {
			annotation.person = $('#person').val();
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property == 'recordedBy') {
			annotation.person = $('#person').val();
			annotation.organismID = $('#organismID').val();
		} else if (annotation.property === 'additionalRecordedBy') {
			annotation.person = $('#person').val();
			annotation.organismID = $('#organismID').val();
			annotation.occurrenceID = $('#occurrenceID').val();
		} else if (annotation.property === "type" && annotation.type != 'taxon') {
			annotation.instance = $('#instance').val();
		} else if (annotation.property === "type" && annotation.type === 'taxon') {
			annotation.belongstotaxon = $('#belongstotaxon').val();
			annotation.rank = $('#rank').val();
		}

		var request = new XMLHttpRequest();
		var body = JSON.stringify(annotation);
		request.open('POST', 'annotation', true);
		request.setRequestHeader('Content-type', 'application/json');
		request.onreadystatechange = function () {
			if (this.readyState === XMLHttpRequest.DONE && this.status === 201) {
				console.log('Created RDF triples.');
			}
		}
		request.send(body);
	},

	addPlugins: function () {
		//Create Plugin
		annotorious.plugin.annoInformationPlugin = function (opt_config_options) { }

		annotorious.plugin.annoInformationPlugin.prototype.initPlugin = function (anno) {
			// Add initialization code here, if needed (or just skip this method if not)
		}
		annotorious.plugin.annoInformationPlugin.prototype.onInitAnnotator = function (annotator) {
			// A Field can be an HTML string or a function(annotation) that returns a string
			annotator.popup.addField(function (annotation) {
				return '<span style="color: white"><pre>' + annotation.verbatim + '</pre></span>';
			});
			annotator.editor.addField(function (annotation) {
				var field = '<input class="annotorious-editor-text" id="verbatim" placeholder="verbatim text" value ="" style="width:160px;height:20px;display:inline;">'
				field = field + '<input class="annotorious-editor-text" id="language" placeholder="language" value ="" style="width:40px;height:20px;display:inline;"><br>'
				field = field + '<select name="property" onchange="semanticAnnotation.addFieldsToEditor($(\'select[name=property]\').val())" class="annotorious-editor-text" style="width:160px;height:20px;display:inline;"><option value="">Select property..</option><option value="type">Type:</option><option value="hasIdentification">Organism identification to:</option><option value="additionalIdentification">Additional identification to:</option><option value="identifiedBy">Organism identified by:</option><option value="recordedBy">Occurrence recorded by:</option><option value="additionalRecordedBy">Additional occurrence recorded by:</option>"><option value="scientificNameAuthorship">Author of scientific name:</option><option value="locatedAt">Occurrence located at:</option><option value="additionalLocatedAt">Additional occurrence located at:</option><option value="verbatimEventDate">Organism described on:</option><option value="basedOn">Identification based on (table):</option><option value="measuresOrDescribes">Table/paragraph measures or describes:</option></select>'
				field = field + '<input class="annotorious-editor-text" id="type" placeholder="type" value ="" style="width:40px;height:20px;display:inline;"><br>'
				field = field + '<span id="taxonfield1"></span>'
				field = field + '<span id="taxonfield2"></span>'
				field = field + '<span id="taxonfield3"></span>'
				field = field + '<span id="datefield1"></span>'
				field = field + '<span id="datefield2"></span>'
				field = field + '<span id="datefield3"></span>'
				field = field + '<span id="anatomicalentityfield1"></span>'
				field = field + '<span id="propertyorattributefield1"></span>'
				field = field + '<span id="geonamesfeaturefield1"></span>'
				field = field + '<span id="organismIDfield1"></span>'
				field = field + '<span id="occurrenceIDfield1"></span>'
				field = field + '<span id="identificationIDfield1"></span>'
				field = field + '<span id="instancefield1"></span>'
				field = field + '<span id="personfield1"></span>'
				return field
			});
		}

		//Add the plugin
		anno.addPlugin('annoInformationPlugin', {});
	},

	addClassToField: function (type) {
		$('#type').val(type);
	},

	addFieldsToEditor: function (property) {

		//empty all fields
		$('#taxonfield1').html("");
		$('#taxonfield2').html("");
		$('#taxonfield3').html("");
		$('#datefield1').html("");
		$('#datefield2').html("");
		$('#datefield3').html("");
		$('#anatomicalentityfield1').html("");
		$('#propertyorattributefield1').html("");
		$('#geonamesfeaturefield1').html("");
		$('#organismIDfield1').html("");
		$('#occurrenceIDfield').html("");
		$('#identificationIDfield1').html("");
		$('#instancefield1').html("");
		$('#personfield1').html("");

		if (property === "hasIdentification") {
			$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
			$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
			$('#taxonfield3').html('<input class="annotorious-editor-text" id="identifiedBy" placeholder="Identified By:" value =""  style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
		} else if (property === 'additionalIdentification') {
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			$('#identificationIDfield1').html('<input class="annotorious-editor-text" id="identificationID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
			$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
			$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
		} else if (property === 'verbatimEventDate') {
			$('#datefield1').html('<input class="annotorious-editor-text" id="year" placeholder="Year (yyyy):" value ="0" style="width:250px;height:20px;">');
			$('#datefield2').html('<input class="annotorious-editor-text" id="month" placeholder="Month (mm):" value ="0" style="width:250px;height:20px;">');
			$('#datefield3').html('<input class="annotorious-editor-text" id="day" placeholder="Day (dd):" value ="0" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
		} else if (property === 'basedOn') {
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
		} else if (property === 'measuresOrDescribes') {
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			if ($('#type').val() === 'propertyorattribute') {
				$('#propertyorattributefield1').html('<input class="annotorious-editor-text" id="propertyorattribute" placeholder="ncit:propertyorattribute subclass IRI:" value ="" style="width:250px;height:20px;">');
			} else if ($('#type').val() === "anatomicalentity") {
				$('#anatomicalentityfield1').html('<input class="annotorious-editor-text" id="anatomicalentity" placeholder="uberon:anatomicalentity subclass IRI:" value ="" style="width:250px;height:20px;">');
			}
		} else if (property == "locatedAt") {
			$('#geonamesfeaturefield1').html('<input class="annotorious-editor-text" id="geonamesfeature" placeholder="gn:geonamesfeature IRI:" value ="" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:125px;height:20px;display:inline;">');
		} else if (property == "additionalLocatedAt") {
			$('#geonamesfeaturefield1').html('<input class="annotorious-editor-text" id="geonamesfeature" placeholder="gn:geonamesfeature IRI:" value ="" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			$('#occurrenceIDfield1').html('<input class="annotorious-editor-text" id="occurrenceID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
		} else if (property === 'scientificNameAuthorship') {
			$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
			$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
		} else if (property == 'identifiedBy') {
			$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
		} else if (property == 'recordedBy') {
			$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
		} else if (property === 'additionalRecordedBy') {
			$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
			$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			$('#occurrenceIDfield1').html('<input class="annotorious-editor-text" id="occurrenceID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
		} else if (property == "type" && $('#type').val() != 'taxon') {
			$('#instancefield1').html('<input class="annotorious-editor-text" id="instance" placeholder="Instance:" value ="" style="width:250px;height:20px;">');
		} else if (property == "type" && $('#type').val() == 'taxon') {
			$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
			$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
		}
	}
}
