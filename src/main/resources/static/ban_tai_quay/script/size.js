angular.module("myApp").controller("sizeCtrl", function ($scope, $http) {

    const API = "http://localhost:8084/san-pham/size";

    // =====================
    // STATE
    // =====================
    $scope.dsSize = [];

    $scope.form = { trangThai: 1, moTa: "" };
    $scope.detail = null;

    $scope.isEdit = false;
    $scope.submitted = false;

    $scope.err = { maSZ: "", tenSZ: "" };

    // FILTER
    $scope.keyword = "";
    $scope.filterTrangThai = "";
    $scope.filterErr = "";
    $scope.filterSubmitted = false;

    // Suggest
    $scope.suggest = [];

    // =====================
    // HELPERS
    // =====================
    function clearErr() {
        $scope.err.maSZ = "";
        $scope.err.tenSZ = "";
    }

    function norm(s) {
        return (s || "").toString().trim().toLowerCase();
    }

    function isValidMa(ma) {
        if (!ma) return false;
        return /^[A-Za-z0-9_-]+$/.test(ma.trim());
    }

    function isValidTen(ten) {
        if (!ten) return false;
        return /^[A-Za-zÀ-ỹ0-9 \/]+$/.test(ten.trim());
    }

    function extractBackendMessage(err) {
        if (!err) return "";
        if (err.data) {
            if (typeof err.data === "string") return err.data;
            if (err.data.message) return err.data.message;
            if (err.data.error) return err.data.error;
        }
        return err.status ? ("HTTP " + err.status) : "";
    }

    function buildSuggest() {
        const map = {};
        ($scope.dsSize || []).forEach(x => {
            const t = (x.tenSZ || "").trim();
            if (t) map[t.toLowerCase()] = t;
        });
        $scope.suggest = Object.values(map).slice(0, 12);
    }

    function isDuplicateMa(ma, currentId) {
        const m = norm(ma);
        return ($scope.dsSize || []).some(x => x.id !== currentId && norm(x.maSZ) === m);
    }

    function isDuplicateTen(ten, currentId) {
        const t = norm(ten);
        return ($scope.dsSize || []).some(x => x.id !== currentId && norm(x.tenSZ) === t);
    }

    // LocalDateTime -> dd/MM/yyyy HH:mm:ss (cho modal chi tiết)
    $scope.formatDateTime = function (val) {
        if (!val) return "";
        const d = new Date(val);
        if (!isNaN(d.getTime())) {
            const pad = n => (n < 10 ? "0" + n : "" + n);
            return (
                pad(d.getDate()) + "/" + pad(d.getMonth() + 1) + "/" + d.getFullYear() +
                " " + pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(d.getSeconds())
            );
        }
        return val;
    };

    // =====================
    // LOAD
    // =====================
    $scope.load = function () {
        const params = {};

        if ($scope.keyword && $scope.keyword.trim()) params.ten = $scope.keyword.trim();
        if ($scope.filterTrangThai !== "" && $scope.filterTrangThai !== null && $scope.filterTrangThai !== undefined) {
            params.trangThai = Number($scope.filterTrangThai);
        }

        $http.get(API, { params }).then(res => {
            $scope.dsSize = res.data || [];
            buildSuggest();
        }).catch(err => {
            console.error("Lỗi load size:", err);
            alert(extractBackendMessage(err) || "Không load được danh sách size!");
        });
    };

    $scope.load();

    // =====================
    // FILTER
    // =====================
    $scope.onKeywordChange = function () {
        $scope.filterSubmitted = false;
        $scope.filterErr = "";
        buildSuggest();
    };

    $scope.search = function () {
        $scope.filterSubmitted = true;
        $scope.filterErr = "";

        const hasKeyword = !!($scope.keyword && $scope.keyword.trim());
        const hasTrangThai = ($scope.filterTrangThai !== "" && $scope.filterTrangThai !== null && $scope.filterTrangThai !== undefined);

        if (!hasKeyword && !hasTrangThai) {
            $scope.filterErr = "Vui lòng nhập từ khóa hoặc chọn trạng thái để tìm";
            return;
        }

        if (hasKeyword && !isValidTen($scope.keyword)) {
            $scope.filterErr = "Từ khóa không được chứa ký tự đặc biệt (cho phép “/”)";
            return;
        }

        $scope.load();
    };

    $scope.resetFilter = function () {
        $scope.keyword = "";
        $scope.filterTrangThai = "";
        $scope.filterSubmitted = false;
        $scope.filterErr = "";
        $scope.load();
    };

    // =====================
    // MODALS
    // =====================
    $scope.openAddModal = function () {
        clearErr();
        $scope.isEdit = false;
        $scope.submitted = false;

        $scope.form = { maSZ: "", tenSZ: "", trangThai: 1, moTa: "" };

        if ($scope.szForm) {
            $scope.szForm.$setPristine();
            $scope.szForm.$setUntouched();
        }

        bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).show();
    };

    $scope.openEditModal = function (s) {
        clearErr();
        $scope.isEdit = true;
        $scope.submitted = false;

        $scope.form = angular.copy(s);
        if ($scope.form.trangThai === undefined || $scope.form.trangThai === null) $scope.form.trangThai = 1;
        if ($scope.form.moTa === undefined || $scope.form.moTa === null) $scope.form.moTa = "";

        if ($scope.szForm) {
            $scope.szForm.$setPristine();
            $scope.szForm.$setUntouched();
        }

        bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).show();
    };

    // ✅ ĐỂ Ở NGOÀI save()
    $scope.openDetailModal = function (s) {
        $scope.detail = angular.copy(s);
        bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeDetail")).show();
    };

    // =====================
    // SAVE
    // =====================
    $scope.save = function (form) {
        $scope.submitted = true;
        clearErr();

        if (form) {
            form.$setSubmitted();
            angular.forEach(form.$error, function (fields) {
                angular.forEach(fields, function (f) {
                    if (f && f.$setTouched) f.$setTouched();
                });
            });
        }

        if (form && form.$invalid) return;

        const ma = ($scope.form.maSZ || "").trim();
        const ten = ($scope.form.tenSZ || "").trim();

        if (!isValidMa(ma)) {
            $scope.err.maSZ = "Mã chỉ được gồm chữ/số/_/- và không có khoảng trắng";
            return;
        }
        if (!isValidTen(ten)) {
            $scope.err.tenSZ = "Tên không được chứa ký tự đặc biệt (cho phép “/”)";
            return;
        }

        if (isDuplicateMa(ma, $scope.form.id || null)) {
            $scope.err.maSZ = "Mã size đã tồn tại";
            return;
        }
        if (isDuplicateTen(ten, $scope.form.id || null)) {
            $scope.err.tenSZ = "Tên size đã tồn tại";
            return;
        }

        const payload = angular.copy($scope.form);
        payload.maSZ = ma;
        payload.tenSZ = ten;
        payload.moTa = (payload.moTa || "").toString();
        payload.trangThai = Number(payload.trangThai);

        if (!$scope.isEdit) {
            delete payload.id;

            $http.post(API, payload).then(() => {
                alert("Thêm size thành công!");
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).hide();
                $scope.load();
            }).catch(err => {
                console.error("POST lỗi:", err);
                const msg = extractBackendMessage(err);

                if (msg.toLowerCase().includes("mã")) $scope.err.maSZ = msg;
                else if (msg.toLowerCase().includes("tên")) $scope.err.tenSZ = msg;
                else alert(msg || "Thêm thất bại!");
            });

            return;
        }

        if (!payload.id) {
            alert("Thiếu id để cập nhật!");
            return;
        }

        $http.put(API + "/" + payload.id, payload).then(() => {
            alert("Cập nhật thành công!");
            bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).hide();
            $scope.load();
        }).catch(err => {
            console.error("PUT lỗi:", err);
            const msg = extractBackendMessage(err);

            if (msg.toLowerCase().includes("mã")) $scope.err.maSZ = msg;
            else if (msg.toLowerCase().includes("tên")) $scope.err.tenSZ = msg;
            else alert(msg || "Cập nhật thất bại!");
        });
    };
});
