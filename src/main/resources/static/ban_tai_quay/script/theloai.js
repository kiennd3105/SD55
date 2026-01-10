// theloai.js
angular.module("myApp").controller("theLoaiCtrl", function ($scope, $http, $timeout) {

    // ✅ ĐÚNG THEO CONTROLLER: GET /the-loai/getAll
    const API = "http://localhost:8084/the-loai";

    // ===== DATA =====
    $scope.dsTheLoai = [];
    $scope.allTheLoai = [];

    $scope.form = { trangThai: 1, ma: "", ten: "", moTa: "", kieu: "" };
    $scope.isEdit = false;
    $scope.submitted = false;

    // ✅ có err.kieu
    $scope.err = { ma: "", ten: "", kieu: "" };

    // ===== SEARCH/FILTER =====
    $scope.keyword = "";
    $scope.filterTrangThai = "";
    $scope.searchSubmitted = false;
    $scope.searchErr = "";
    let searchTimer = null;

    // ===== DETAIL =====
    $scope.detail = null;

    // ===== helpers =====
    function clearErr() {
        $scope.err.ma = "";
        $scope.err.ten = "";
        $scope.err.kieu = "";
    }
    function clearSearchErr() {
        $scope.searchErr = "";
    }

    function isValidMa(ma) {
        if (!ma) return false;
        return /^[A-Za-z0-9_-]+$/.test(ma.trim());
    }

    function isValidTen(ten) {
        if (!ten) return false;
        return /^[A-Za-zÀ-ỹ0-9 ]+$/.test(ten.trim());
    }

    // Search: chữ/số/khoảng trắng/_/-
    function isValidSearchKeyword(text) {
        if (!text) return true;
        return /^[A-Za-zÀ-ỹ0-9 _-]+$/.test(text.trim());
    }

    function extractBackendMessage(err) {
        if (err && err.data) {
            if (typeof err.data === "string") return err.data;
            if (err.data.message) return err.data.message;
            if (err.data.error) return err.data.error;
        }
        return "";
    }

    function isDuplicateMa(ma, currentId) {
        const m = (ma || "").trim().toLowerCase();
        if (!m) return false;
        return ($scope.allTheLoai || []).some(x =>
            x.id !== currentId && ((x.ma || "").trim().toLowerCase() === m)
        );
    }

    function isDuplicateTen(ten, currentId) {
        const t = (ten || "").trim().toLowerCase();
        if (!t) return false;
        return ($scope.allTheLoai || []).some(x =>
            x.id !== currentId && ((x.ten || "").trim().toLowerCase() === t)
        );
    }

    // ===== LOAD ALL =====
    $scope.load = function () {
        $http.get(API + "/getAll").then(function (res) {
            $scope.allTheLoai = res.data || [];
            $scope.applyFilterClient();
        }).catch(function (err) {
            console.error("Lỗi load thể loại:", err);
            alert(extractBackendMessage(err) || "Không load được danh sách thể loại!");
        });
    };
    $scope.load();

    $scope.getSTT = function (i) { return i + 1; };

    // ===== FILTER (CLIENT CONTAINS) =====
    $scope.applyFilterClient = function () {
        const kw = ($scope.keyword || "").trim().toLowerCase();
        const tt = $scope.filterTrangThai;

        $scope.dsTheLoai = ($scope.allTheLoai || []).filter(function (x) {
            const ma = (x.ma || "").toLowerCase();
            const ten = (x.ten || "").toLowerCase();
            const kieu = (x.kieu || "").toLowerCase();

            const matchKw = !kw || ma.includes(kw) || ten.includes(kw) || kieu.includes(kw);

            const matchTT =
                (tt === "" || tt === null || tt === undefined)
                    ? true
                    : Number(x.trangThai) === Number(tt);

            return matchKw && matchTT;
        });
    };

    // ✅ bấm Tìm mới bắt buộc nhập keyword
    $scope.applyFilter = function () {
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();

        if (!kw) {
            $scope.searchErr = "Vui lòng nhập nội dung tìm kiếm";
            return;
        }

        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Tìm kiếm không được chứa ký tự đặc biệt";
            return;
        }

        $scope.applyFilterClient();
    };

    // realtime gõ -> lọc (debounce)
    $scope.onKeywordChange = function () {
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();
        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Tìm kiếm không được chứa ký tự đặc biệt";
            return;
        }

        if (searchTimer) $timeout.cancel(searchTimer);
        searchTimer = $timeout(function () {
            $scope.applyFilterClient();
        }, 300);
    };

    $scope.onTrangThaiChange = function () {
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();
        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Tìm kiếm không được chứa ký tự đặc biệt";
            return;
        }

        $scope.applyFilterClient();
    };

    $scope.clearFilter = function () {
        $scope.keyword = "";
        $scope.filterTrangThai = "";
        $scope.searchSubmitted = false;
        clearSearchErr();
        $scope.dsTheLoai = angular.copy($scope.allTheLoai || []);
    };

    $scope.formatDateTime = function (val) {
        if (!val) return "";
        const d = new Date(val);
        if (!isNaN(d.getTime())) {
            const pad = (n) => (n < 10 ? "0" + n : "" + n);
            return (
                pad(d.getDate()) + "/" + pad(d.getMonth() + 1) + "/" + d.getFullYear() +
                " " + pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(d.getSeconds())
            );
        }
        return val;
    };

    // ===== MODAL ADD/EDIT =====
    $scope.openAdd = function () {
        $scope.isEdit = false;
        $scope.submitted = false;
        clearErr();
        $scope.form = { trangThai: 1, ma: "", ten: "", moTa: "", kieu: "" };
        $scope.detail = null;
    };

    $scope.openEdit = function (tl) {
        $scope.isEdit = true;
        $scope.submitted = false;
        clearErr();

        $scope.form = angular.copy(tl);
        if ($scope.form.trangThai === undefined || $scope.form.trangThai === null) $scope.form.trangThai = 1;
        if ($scope.form.moTa === undefined || $scope.form.moTa === null) $scope.form.moTa = "";
        if ($scope.form.kieu === undefined || $scope.form.kieu === null) $scope.form.kieu = "";
        $scope.detail = null;
    };

    $scope.resetForm = function (form) {
        $scope.submitted = false;
        clearErr();

        if (!$scope.isEdit) {
            $scope.form = { trangThai: 1, ma: "", ten: "", moTa: "", kieu: "" };
        }

        if (form) {
            form.$setPristine();
            form.$setUntouched();
        }
    };

    // ===== SAVE (VALIDATE KIỂU BẮT BUỘC) =====
    $scope.save = function (form) {
        $scope.submitted = true;
        clearErr();

        if (form && form.$invalid) return;

        const ma = ($scope.form.ma || "").trim();
        const ten = ($scope.form.ten || "").trim();
        const kieu = ($scope.form.kieu || "").trim();   // ✅ bắt buộc

        if (!isValidMa(ma)) {
            $scope.err.ma = "Mã chỉ gồm chữ/số/_/- và không có khoảng trắng";
            return;
        }
        if (!isValidTen(ten)) {
            $scope.err.ten = "Tên không được chứa ký tự đặc biệt";
            return;
        }
        if (!kieu) {
            $scope.err.kieu = "Vui lòng nhập kiểu quần áo";
            return;
        }

        const currentId = $scope.isEdit ? $scope.form.id : null;
        if (isDuplicateMa(ma, currentId)) {
            $scope.err.ma = "Mã đã tồn tại";
            return;
        }
        if (isDuplicateTen(ten, currentId)) {
            $scope.err.ten = "Tên đã tồn tại";
            return;
        }

        const payload = angular.copy($scope.form);
        payload.ma = ma;
        payload.ten = ten;
        payload.kieu = kieu;
        payload.moTa = (payload.moTa || "").toString();
        payload.trangThai = Number(payload.trangThai);

        // ⚠️ Backend của bạn phải có POST/PUT/GET {id} mới dùng được phần dưới
        if (!$scope.isEdit) {
            delete payload.id;

            $http.post(API, payload).then(function () {
                alert("Thêm thể loại thành công!");
                $scope.load();
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalTheLoaiForm")).hide();
            }).catch(function (err) {
                console.error("Lỗi thêm:", err);
                const msg = extractBackendMessage(err);
                if ((msg || "").toLowerCase().includes("mã")) $scope.err.ma = msg;
                else if ((msg || "").toLowerCase().includes("tên")) $scope.err.ten = msg;
                else if ((msg || "").toLowerCase().includes("kiểu")) $scope.err.kieu = msg;
                else alert(msg || "Thêm thất bại!");
            });

        } else {
            if (!payload.id) { alert("Thiếu id để cập nhật!"); return; }

            $http.put(API + "/" + payload.id, payload).then(function () {
                alert("Cập nhật thành công!");
                $scope.load();
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalTheLoaiForm")).hide();
            }).catch(function (err) {
                console.error("Lỗi cập nhật:", err);
                const msg = extractBackendMessage(err);
                if ((msg || "").toLowerCase().includes("mã")) $scope.err.ma = msg;
                else if ((msg || "").toLowerCase().includes("tên")) $scope.err.ten = msg;
                else if ((msg || "").toLowerCase().includes("kiểu")) $scope.err.kieu = msg;
                else alert(msg || "Cập nhật thất bại!");
            });
        }
    };

    // ===== DETAIL =====
    $scope.showDetail = function (tl) {
        $scope.detail = null;
        if (!tl || !tl.id) return;

        $http.get(API + "/" + tl.id).then(function (res) {
            $scope.detail = res.data;
        }).catch(function () {
            $scope.detail = angular.copy(tl);
        });
    };

});
