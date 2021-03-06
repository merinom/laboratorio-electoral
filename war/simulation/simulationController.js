Laboratory.controller('simulationController',['$scope', '$http','$routeParams', '$location', '$uibModal','breadcrumbs',
                                              function($scope,$http,$routeParams,$location,$uibModal,breadcrumbs){
    breadcrumbs.setBreadcrumbs([breadcrumbs.createBreadcrumb("project", {projectId:$routeParams.projectId}),breadcrumbs.createBreadcrumb("simulation", {projectId:$routeParams.projectId,simulationId:$routeParams.simulationId})]);

    $scope.$on("checkAuth",function(){
		if(!$scope.appUser){
			$location.path("/");
		}
	});
	$scope.simulation={
			id:$routeParams.simulationId,
			name:"Simulacion de ejemplo",
			Circunscriptions:[]
	};
	
	$scope.newVotingIntent={party:{}};
	
	var reloadSimulation = function(){
		$http.get("/api/simulations",{params:{id:$routeParams.simulationId}}).success(function(data,status){
			$scope.cleanObjectFromDatabase(data);
			$scope.simulation = data;
			console.log($scope.simulation);
		});
	};
	
	var canReadFile = function(file, onLoadCallback){
		if( file != undefined){
			fr = new FileReader();
			fr.readAsText(file[0]);
			fr.onloadend = onLoadCallback;
		}
	};
	
	var isJSON = function (jsonString){
	    try {
	        var o = JSON.parse(jsonString);

	        if (o && typeof o === "object" && o !== null) {
	            return true;
	        }
	    }
	    catch (e) { }

	    return false;
	};
	
	var isGEOJson = function(geoJson){
		if (geoJson.type != undefined && geoJson.geometry != undefined){
			return true;
		}
		return false;
	};
	
	//This method basically watches the file input, and checks if input file is a GeoJSON
	$scope.$watchCollection("files",function(newValue){
		if(newValue != undefined && newValue.length != 0){
			canReadFile(newValue, function(e){
				try {

					if(isJSON(e.target.result)){
						
						var json = JSON.parse(e.target.result);
						
						if (isGEOJson(json)){
							$scope.selectedCircumscription.localization = e.target.result;
							$scope.selectedCircumscription.localizationFilename = newValue[0].name;
							$scope.$apply();
						} else {
							$("#topojson").val('');
							$scope.$apply();
							alert("It is not a GEOJson File.");
						}

					} else {
						$("#topojson").val('');
						$scope.$apply();
						alert("It is not a Json File.");
					}
				} catch(e){
					$("#topojson").val('');
					$scope.$apply();
					alert("It is not a text File");
					//si no falta pues se elimina y listo
				}
			});
		}
	});
	
	$scope.$watch("selectedCircumscription",function(newValue){
		$("#topojson").val('');
	});
	
	$scope.addCircumscription = function(){
		var circumscription = {name:"New circunscription",polled:0,population:0,votingIntents:[]};
		$scope.simulation.Circunscriptions.push(circumscription);
		$scope.selectedCircumscription =circumscription;
	};

	$scope.deleteCircumscription = function(){
		var index = $scope.simulation.Circunscriptions.indexOf($scope.selectedCircumscription );
		if(index!=-1){
			$scope.simulation.Circunscriptions.splice(index,1);
			$scope.selectedCircumscription = undefined;
		}
	};
	
	$scope.deleteTopojson = function(selectedCircumscription){
		selectedCircumscription.localization = null;
		selectedCircumscription.localizationFilename = null;
		$("#topojson").val('');
	};

	$scope.insertVotingIntent = function(){
		if($scope.addVotingIntentForm.$valid){
			$scope.selectedCircumscription.votingIntents.push($scope.newVotingIntent);
			$scope.newVotingIntent={party:{}};
		}
	};

	$scope.deleteVotingIntent = function(votingIntent){
		var index = $scope.selectedCircumscription.votingIntents.indexOf(votingIntent);
		if(index!=-1){
			$scope.selectedCircumscription.votingIntents.splice(index,1);
		}
	};

	$scope.saveSimulation = function(){
		$http.put("/api/simulations",$scope.simulation).success(function(data,status){
			alert("Simulation updated");
		});
	};

	reloadSimulation();
	
	$scope.animationsEnabled = true;
	$scope.createReport = function(){
		var modalInstance = $uibModal.open({
			animation: $scope.animationsEnabled,
			templateUrl: 'modalView.html',
			controller: 'modalController'
		});
	
	
	modalInstance.result.then(
			function(resultCreated){
				$location.path("/projects/"+$routeParams.projectId+"/results/"+resultCreated.id);
			}
		);
	
	}
}]);


Laboratory.controller('modalController',['$scope', '$http','$routeParams', '$location', '$uibModal', '$uibModalInstance',
                                         function($scope,$http,$routeParams,$location,$uibModal, $uibModalInstance){
   $scope.methods = {"values": [{
   						"value":"dhondt",
   						"name":"Hont Method"
   					},{
   						"value":"saint",
   						"name":"Saints method"
   					}],
   					"defaultvalue": {"value":"dhondt","name":"Hont Method"}
   				    };

	$scope.ok = function (simulation) {
		$http({
			method: 'POST',
			url: '/api/reports',
			data:{ name: $scope.nameReport, method: $scope.methods.defaultvalue.value },
			headers: {'Content-Type': 'application/json'},
			params: {simulation: $routeParams.simulationId,projectId:$routeParams.projectId}
		}).success(function(dataReturned){
			$scope.cleanObjectFromDatabase(dataReturned);
			$uibModalInstance.close(dataReturned);
			}).error(function(data, status){
			if(status == 401){
				alert("Denied access without session");
			}else if(status == 400){
				alert("Complete all the fields");
			}
		});
	};

	$scope.cancel = function () {
	   $uibModalInstance.dismiss("Canceled");
	};
	
}]);
