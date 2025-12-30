// size.js
angular.module("myApp").controller("sizeCtrl", function ($scope, $http) {

    const API = "http://localhost:8084/san-pham/size";

    $scope.dsSize = [];

    // FORM MODAL
    $scope.form = { trangThai: 1 };
    $scope.isEdit = false;
    $scope.submitted = false;

    // FILTER
    $scope.keyword = "";
    $scope.filterTrangThai = ""; // ""=Tất cả, "1"=Hoạt động, "0"=Ngừng
    $scope.filterSubmitted = false;
    $scope.filterErr = "";

    // Suggest list (autocomplete)
    $scope.suggest = [];

    // DETAIL
    $scope.detail = null;

    // ERROR MESSAGE form
    $scope.err = { maSZ: "", tenSZ: "" };

    // =====================
    // HELPERS
    // =====================
    function clearErr() {
        $scope.err.maSZ = "";
        $scope.err.tenSZ = "";
    }

    function clearFilterErr() {
        $scope.filterErr = "";
    }

    function norm(s) {
        return (s || "").toString().trim().toLowerCase();
    }

    // MA: chỉ chữ/số/_/- , không khoảng trắng
    function isValidMa(ma) {
        if (!ma) return false;
        return /^[A-Za-z0-9_-]+$/.test(ma.trim());
    }

    // keyword/tên size: cho phép chữ/số/space và "/"
    function isValidKeyword(kw) {
        if (!kw) return false;
        return /^[A-Za-zÀ-ỹ0-9 \/]+$/.test(kw.trim());
    }

    function isDuplicateMa(ma, currentId) {
        const m = norm(ma);
        if (!m) return false;
        return ($scope.dsSize || []).some(x => x.id !== currentId && norm(x.maSZ) === m);
    }

    function isDuplicateTen(ten, currentId) {
        const t = norm(ten);
        if (!t) return false;
        return ($scope.dsSize || []).some(x => x.id !== currentId && norm(x.tenSZ) === t);
    }

    function extractBackendMessage(err) {
        if (err && err.data) {
            if (typeof err.data === "string") return err.data;
            if (err.data.message) return err.data.message;
        }
        return "";
    }

    // LocalDateTime -> dd/MM/yyyy HH:mm:ss
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

    // =====================
    // LOAD (GET ALL / SEARCH / FILTER)
    // =====================
    $scope.load = function () {
        const params = {};
        if ($scope.keyword && $scope.keyword.trim()) params.ten = $scope.keyword.trim();
        if ($scope.filterTrangThai !== "" && $scope.filterTrangThai !== null && $scope.filterTrangThai !== undefined) {
            params.trangThai = Number($scope.filterTrangThai);
        }

        $http.get(API, { params }).then(function (res) {
            $scope.dsSize = res.data || [];
            buildSuggest(); // update gợi ý theo data đang có
        }).catch(function (err) {
            console.error("Lỗi load size", err);
            alert("Không load được danh sách size!");
        });
    };

    // load all ban đầu
    $scope.loadAllForSuggest = function () {
        $http.get(API).then(function (res) {
            // lưu 1 bản all để gợi ý luôn chuẩn
            $scope._all = res.data || [];
            buildSuggestFromAll();
        }).catch(function () {
            $scope._all = [];
            $scope.suggest = [];
        });
    };

    $scope.load();
    $scope.loadAllForSuggest();

    $scope.getSTT = function (i) { return i + 1; };

    // =====================
    // AUTOCOMPLETE "liên quan" khi gõ
    // =====================
    function buildSuggest() {
        // gợi ý từ list hiện tại
        const map = {};
        ($scope.dsSize || []).forEach(function (x) {
            const t = (x.tenSZ || "").trim();
            if (t) map[t.toLowerCase()] = t;
        });
        $scope.suggest = Object.values(map).slice(0, 12);
    }

    function buildSuggestFromAll() {
        const map = {};
        ($scope._all || []).forEach(function (x) {
            const t = (x.tenSZ || "").trim();
            if (t) map[t.toLowerCase()] = t;
        });
        $scope.suggest = Object.values(map).slice(0, 12);
    }

    // gõ -> update gợi ý theo prefix
    $scope.onKeywordChange = function () {
        $scope.filterSubmitted = false;
        clearFilterErr();

        const kw = norm($scope.keyword);
        if (!kw) {
            buildSuggestFromAll();
            return;
        }

        // validate realtime: nếu chứa ký tự đặc biệt -> báo nhẹ
        if (!isValidKeyword($scope.keyword)) {
            $scope.filterErr = "Từ khóa không được chứa ký tự đặc biệt";
            $scope.filterSubmitted = true;
        }

        // lọc suggest theo prefix (bắt đầu bằng)
        const list = ($scope._all || []).map(x => (x.tenSZ || "").trim()).filter(Boolean);
        const uniq = {};
        list.forEach(function (t) {
            const low = t.toLowerCase();
            if (low.startsWith(kw)) uniq[low] = t;
        });
        $scope.suggest = Object.values(uniq).slice(0, 12);
    };

    // =====================
    // SEARCH / RESET
    // =====================
    $scope.search = function () {
        $scope.filterSubmitted = true;
        clearFilterErr();

        const hasKeyword = !!($scope.keyword && $scope.keyword.trim());
        const hasTrangThai = ($scope.filterTrangThai !== "" && $scope.filterTrangThai !== null && $scope.filterTrangThai !== undefined);

        if (!hasKeyword && !hasTrangThai) {
            $scope.filterErr = "Vui lòng nhập từ khóa hoặc chọn trạng thái để tìm";
            return;
        }

        if (hasKeyword && !isValidKeyword($scope.keyword)) {
            $scope.filterErr = "Từ khóa không được chứa ký tự đặc biệt";
            return;
        }

        $scope.load();
    };

    $scope.resetFilter = function () {
        $scope.keyword = "";
        $scope.filterTrangThai = "";
        $scope.filterSubmitted = false;
        clearFilterErr();
        $scope.load(); // load lại full list
        buildSuggestFromAll();
    };

    // =====================
    // MODALS
    // =====================
    $scope.openAddModal = function () {
        clearErr();
        $scope.isEdit = false;
        $scope.submitted = false;
        $scope.form = { trangThai: 1, moTa: "" };

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

        if ($scope.szForm) {
            $scope.szForm.$setPristine();
            $scope.szForm.$setUntouched();
        }

        bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).show();
    };

    $scope.openDetailModal = function (s) {
        $scope.detail = angular.copy(s);
        bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeDetail")).show();
    };

    // =====================
    // SAVE (ADD/UPDATE)
    // =====================
    $scope.save = function (form) {
        $scope.submitted = true;
        clearErr();

        if (form && form.$invalid) return;

        const ma = ($scope.form.maSZ || "").trim();
        const ten = ($scope.form.tenSZ || "").trim();

        if (!isValidMa(ma)) {
            $scope.err.maSZ = "Mã chỉ được gồm chữ/số/_/- và không có khoảng trắng";
            return;
        }
        if (!isValidKeyword(ten)) {
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

        let payload = angular.copy($scope.form);
        payload.maSZ = ma;
        payload.tenSZ = ten;
        payload.trangThai = Number(payload.trangThai);

        if (!$scope.isEdit) {
            delete payload.id;
            $http.post(API, payload).then(function () {
                alert("Thêm size thành công!");
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).hide();
                $scope.load();
                $scope.loadAllForSuggest();
            }).catch(function (err) {
                const msg = extractBackendMessage(err);
                if (msg.includes("Mã")) $scope.err.maSZ = msg;
                else if (msg.includes("Tên")) $scope.err.tenSZ = msg;
                else alert(msg || "Thêm thất bại!");
            });
            return;
        }

        if (!payload.id) {
            alert("Thiếu id để cập nhật!");
            return;
        }

        $http.put(API + "/" + payload.id, payload).then(function () {
            alert("Cập nhật thành công!");
            bootstrap.Modal.getOrCreateInstance(document.getElementById("modalSizeForm")).hide();
            $scope.load();
            $scope.loadAllForSuggest();
        }).catch(function (err) {
            const msg = extractBackendMessage(err);
            if (msg.includes("Mã")) $scope.err.maSZ = msg;
            else if (msg.includes("Tên")) $scope.err.tenSZ = msg;
            else alert(msg || "Cập nhật thất bại!");
        });
    };

    // =====================
    // DELETE
    // =====================
    $scope.remove = function (s) {
        if (!s || !s.id) return;
        if (!confirm("Bạn chắc chắn muốn xóa size này?")) return;

        $http.delete(API + "/" + s.id).then(function () {
            alert("Xóa thành công!");
            $scope.load();
            $scope.loadAllForSuggest();
        }).catch(function (err) {
            alert(extractBackendMessage(err) || "Xóa thất bại!");
        });
    };

});
