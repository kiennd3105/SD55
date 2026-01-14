angular.module("myApp").controller("mauSacCtrl", function ($scope, $http, $timeout) {

    const API_URL = "http://localhost:8084/san-pham/mau-sac";
    let modalInstance = null;

    $scope.dsMauSac = [];
    $scope.mauSacMoi = {};
    $scope.mauSacView = {};
    $scope.search = { tenM: '', trangThai: null };
    $scope.loi = "";
    $scope.isEdit = false;
    $scope.dangGui = false;

    $timeout(function() {
        modalInstance = new bootstrap.Modal(document.getElementById('modalMauSac'));
    });

    function loadData() {
        $http.get(API_URL).then(function (res) {
            $scope.dsMauSac = res.data || [];
        }).catch(function (err) {
            console.error("Không thể kết nối đến máy chủ", err);
        });
    }
    loadData();

    let timeoutPromise = null;
    $scope.timKiem = function () {
        if (timeoutPromise) $timeout.cancel(timeoutPromise);

        timeoutPromise = $timeout(function () {
            $http.get(API_URL + "/search", {
                params: {
                    ten: $scope.search.tenM?.trim() || null,
                    trangThai: $scope.search.trangThai
                }
            }).then(function (res) {
                $scope.dsMauSac = res.data || [];
            });
        }, 400);
    };

    $scope.reset = function () {
        $scope.search = { tenM: '', trangThai: null };
        loadData();
    };

    $scope.moModalThem = function () {
        $scope.isEdit = false;
        $scope.loi = "";
        $scope.mauSacMoi = {
            trangThai: 1
        };
        modalInstance.show();
    };

    $scope.moModalSua = function (id) {
        $scope.isEdit = true;
        $scope.loi = "";
        $http.get(API_URL + "/" + id).then(function (res) {
            $scope.mauSacMoi = angular.copy(res.data);
            modalInstance.show();
        }).catch(function () {
            alert("Lỗi: Không tìm thấy dữ liệu màu sắc!");
        });
    };

    $scope.xemChiTiet = function (id) {
        $http.get(API_URL + "/" + id).then(function (res) {
            $scope.mauSacView = res.data;
            const viewModal = new bootstrap.Modal(document.getElementById('modalXemChiTiet'));
            viewModal.show();
        });
    };

    $scope.luuMauSac = function () {
        if (!$scope.mauSacMoi.tenM) {
            $scope.loi = "Vui lòng nhập tên màu sắc!";
            return;
        }

        $scope.dangGui = true;
        $scope.loi = "";

        let request;
        console.log($scope.mauSacMoi)
        if ($scope.isEdit) {
            console.log("Cập nhật màu sắc")
            request = $http.put(API_URL + "/" + $scope.mauSacMoi.id, $scope.mauSacMoi);
        } else {
            console.log("thêm mới màu sắc")
            request = $http.post(API_URL, $scope.mauSacMoi);
        }

        request.then(function () {
            modalInstance.hide();
            loadData();
            alert($scope.isEdit ? "Cập nhật thành công!" : "Thêm mới thành công!");
        }).catch(function (err) {
            console.log("full error"+err)
            $scope.loi =
                err.data?.message ||
                "Đã xảy ra lỗi!";
        }).finally(function () {
            $scope.dangGui = false;
        });
    };

    $scope.xoaMauSac = function (th) {
        if (!th || !th.id) return;

        const confirmMessage = "Bạn có chắc muốn xóa màu sắc '" + (th.tenM || "") + "' không?";
        if (!confirm(confirmMessage)) return;

        $http.delete(API_URL + "/" + encodeURIComponent(th.id))
            .then(function (response) {
                alert("Xóa thành công!");
                loadData();
            })
            .catch(function (err) {
                console.error("Lỗi xóa màu sắc", err);
                console.error("Status:", err.status);
                console.error("Data:", err.data);
                console.error("StatusText:", err.statusText);
                const errorMessage = err.data || err.statusText || "Xóa thất bại!";
                alert(errorMessage);
            });
    };
});