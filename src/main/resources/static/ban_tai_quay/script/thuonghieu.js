angular.module("myApp").controller("thuongHieuCtrl",
    function ($scope, $http, $timeout) {

        const API_URL = "http://localhost:8084/san-pham/thuong-hieu";

        // ================= STATE =================
        $scope.dsThuongHieu = [];
        $scope.thuongHieuMoi = {};
        $scope.search = { tenTH: '', trangThai: null };
        $scope.loi = "";
        $scope.dangGui = false;

        $scope.isEdit = false;
        $scope.isView = false;

        // ================= LOAD DATA =================
        function loadData() {
            $http.get(API_URL)
                .then(res => {
                    // $scope.dsThuongHieu = res.data || [];
                    console.log(res.data)
                    $scope.dsThuongHieu.length = 0;

                    Array.prototype.push.apply(
                        $scope.dsThuongHieu,
                        res.data || []
                    );
                })
                .catch(err => {
                    console.error("Lỗi load data", err);
                });
        }
        loadData();

        // ================= SEARCH =================
        let searchTimeout = null;
        $scope.timKiem = function () {
            if (searchTimeout) $timeout.cancel(searchTimeout);

            searchTimeout = $timeout(() => {
                $http.get(API_URL + "/search", {
                    params: {
                        ten: $scope.search.tenTH?.trim() || null,
                        trangThai: $scope.search.trangThai
                    }
                }).then(res => {
                    $scope.dsThuongHieu = res.data || [];
                });
            }, 300);
        };

        $scope.reset = function () {
            $scope.search = { tenTH: '', trangThai: null };
            loadData();
        };

        // ================= OPEN MODAL ADD =================
        $scope.moModalThem = function () {
            $scope.isEdit = false;
            $scope.isView = false;
            $scope.dangGui = false;
            $scope.loi = "";

            $scope.thuongHieuMoi = {
                tenTH: "",
                trangThai: "",
                moTa: ""
            };

            new bootstrap.Modal(
                document.getElementById("modalThemThuongHieu")
            ).show();
        };

        // ================= OPEN MODAL EDIT (THEO ID) =================
        $scope.moModalSua = function (id) {
            $scope.isEdit = true;
            $scope.isView = false;
            $scope.dangGui = false;
            $scope.loi = "";

            const encodedId = encodeURIComponent(id);

            $http.get(API_URL + "/" + encodedId)
                .then(res => {
                    $scope.thuongHieuMoi = res.data;

                    // Hiển thị modal Sửa
                    new bootstrap.Modal(
                        document.getElementById("modalChinhSua")
                    ).show();
                })
                .catch(err => {
                    console.error("Lỗi lấy chi tiết", err);
                    alert("Không lấy được dữ liệu chỉnh sửa");
                });
        };

        // ================= VIEW DETAIL (THEO ID) =================
        $scope.xemChiTiet = function (id) {
            $http.get(API_URL + "/" + encodeURIComponent(id))
                .then(res => {
                    $scope.thuongHieuView = res.data; // Bind dữ liệu vào view riêng
                    new bootstrap.Modal(document.getElementById("modalXemChiTiet")).show();
                })
                .catch(err => {
                    console.error("Lỗi xem chi tiết", err);
                    alert("Không lấy được dữ liệu chi tiết");
                });
        };


        // ================= SAVE (ADD / UPDATE) =================
        $scope.luuThuongHieu = function () {
            if ($scope.isView || $scope.dangGui) return;

            $scope.dangGui = true;
            $scope.loi = "";

            const payload = {
                tenTH: $scope.thuongHieuMoi.tenTH?.trim(),
                trangThai: $scope.thuongHieuMoi.trangThai,
                moTa: $scope.thuongHieuMoi.moTa?.trim() || null
            };

            let request;
            if ($scope.isEdit) {
                // SỬ DỤNG id LÀ KHÓA CHÍNH
                request = $http.put(
                    API_URL + "/" + encodeURIComponent($scope.thuongHieuMoi.id),
                    payload
                );
            } else {
                request = $http.post(API_URL, payload);
            }

            request.then(() => {
                // Đóng modal tương ứng
                if ($scope.isEdit) {
                    bootstrap.Modal
                        .getInstance(document.getElementById("modalChinhSua"))
                        .hide();
                } else {
                    bootstrap.Modal
                        .getInstance(document.getElementById("modalThemThuongHieu"))
                        .hide();
                }

                loadData();
            }).catch(err => {
                if (err.status === 400) {
                    $scope.loi = err.data.message;
                }
                else {
                    alert("Lỗi hệ thống");
                    console.error(err);
                }
            }).finally(() => {
                $scope.dangGui = false;
            });
        };

        // ================= DELETE =================
        $scope.xoaThuongHieu = function (th) {
            if (!th || !th.id) return;

            const confirmMessage = "Bạn có chắc muốn xóa thương hiệu '" + (th.tenTH || "") + "' không?";
            if (!confirm(confirmMessage)) return;

            $http.delete(API_URL + "/" + encodeURIComponent(th.id))
                .then(function (response) {
                    alert("Xóa thành công!");
                    loadData();
                })
                .catch(function (err) {
                    console.error("Lỗi xóa thương hiệu", err);
                    console.error("Status:", err.status);
                    console.error("Data:", err.data);
                    console.error("StatusText:", err.statusText);
                    const errorMessage = err.data || err.statusText || "Xóa thất bại!";
                    alert(errorMessage);
                });
        };
    }
);
