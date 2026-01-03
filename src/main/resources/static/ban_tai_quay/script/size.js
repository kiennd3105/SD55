// size.js
angular.module("myApp").controller("sizeCtrl", function ($scope, $http) {

    const API = "http://localhost:8084/san-pham/size";

    $scope.dsSize = [];
    $scope.form = { trangThai: 1 };
    $scope.isEdit = false;
    $scope.submitted = false;

    // filter tìm kiếm
    $scope.filter = {
        ten: "",
        trangThai: "" // "" = tất cả
    };

    // =========================
    // LOAD (có filter)
    // =========================
    $scope.load = function () {
        let params = {};

        if ($scope.filter.ten && $scope.filter.ten.trim() !== "") {
            params.ten = $scope.filter.ten.trim();
        }
        if ($scope.filter.trangThai !== "" && $scope.filter.trangThai !== null && $scope.filter.trangThai !== undefined) {
            params.trangThai = Number($scope.filter.trangThai);
        }

        $http.get(API, { params: params }).then(function (res) {
            $scope.dsSize = res.data || [];
        }).catch(function (err) {
            console.error("Lỗi load size", err);
            alert("Không load được danh sách size!");
        });
    };
    $scope.load();

    $scope.getSTT = function (i) { return i + 1; };

    // =========================
    // SEARCH + RESET FILTER
    // =========================
    $scope.search = function () {
        $scope.load();
    };

    $scope.resetFilter = function () {
        $scope.filter = { ten: "", trangThai: "" };
        $scope.load();
    };

    // =========================
    // EDIT
    // =========================
    $scope.edit = function (s) {
        $scope.form = angular.copy(s);
        if ($scope.form.trangThai === undefined || $scope.form.trangThai === null) {
            $scope.form.trangThai = 1;
        }
        $scope.isEdit = true;
        $scope.submitted = false;
    };

    // =========================
    // RESET FORM
    // =========================
    $scope.reset = function (form) {
        $scope.form = { trangThai: 1 };
        $scope.isEdit = false;
        $scope.submitted = false;

        if (form) {
            form.$setPristine();
            form.$setUntouched();
        }
    };

    // =========================
    // ADD
    // =========================
    $scope.add = function (form) {
        $scope.submitted = true;
        if (form && form.$invalid) return;

        let payload = angular.copy($scope.form);
        delete payload.id;
        payload.maSZ = (payload.maSZ || "").trim();
        payload.tenSZ = (payload.tenSZ || "").trim();
        payload.trangThai = Number(payload.trangThai);

        $http.post(API, payload).then(function () {
            alert("Thêm size thành công!");
            $scope.load();
            $scope.reset(form);
        }).catch(function (err) {
            console.error("Lỗi thêm size", err);

            // backend trả 409: mã/tên trùng
            if (err.status === 409 && err.data) {
                alert(err.data);
                return;
            }

            alert("Thêm thất bại!");
        });
    };

    // =========================
    // UPDATE
    // =========================
    $scope.update = function (form) {
        $scope.submitted = true;
        if (form && form.$invalid) return;

        if (!$scope.form.id) {
            alert("Thiếu id để cập nhật!");
            return;
        }

        let payload = angular.copy($scope.form);
        payload.maSZ = (payload.maSZ || "").trim();
        payload.tenSZ = (payload.tenSZ || "").trim();
        payload.trangThai = Number(payload.trangThai);

        $http.put(API + "/" + payload.id, payload).then(function () {
            alert("Cập nhật thành công!");
            $scope.load();
            $scope.reset(form);
        }).catch(function (err) {
            console.error("Lỗi cập nhật size", err);

            if (err.status === 409 && err.data) {
                alert(err.data);
                return;
            }

            alert("Cập nhật thất bại!");
        });
    };

});
