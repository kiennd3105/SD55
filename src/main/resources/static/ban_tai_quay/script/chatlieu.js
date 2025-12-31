// chatlieu.js
angular.module("myApp").controller("chatLieuCtrl", function ($scope, $http, $timeout) {

    const API = "http://localhost:8084/san-pham/chat-lieu";

    // ===== DATA =====
    $scope.dsChatLieu = [];        // danh sách hiển thị
    $scope.allChatLieu = [];       // danh sách gốc (lọc client)
    $scope.form = { trangThai: 1 };
    $scope.isEdit = false;
    $scope.submitted = false;
    $scope.err = { maCL: "", tenCL: "" };

    // ===== SEARCH/FILTER =====
    $scope.keyword = "";
    $scope.filterTrangThai = "";     // "" | "1" | "0"
    $scope.searchSubmitted = false;
    $scope.searchErr = "";
    let searchTimer = null;

    // ===== DETAIL =====
    $scope.detail = null;

    // ===== helpers =====
    function clearErr() {
        $scope.err.maCL = "";
        $scope.err.tenCL = "";
    }
    function clearSearchErr() {
        $scope.searchErr = "";
    }

    // Mã: chỉ chữ/số/_/- , không khoảng trắng
    function isValidMa(ma) {
        if (!ma) return false;
        return /^[A-Za-z0-9_-]+$/.test(ma.trim());
    }

    // Tên: chữ có dấu + số + khoảng trắng (không ký tự đặc biệt)
    function isValidTen(ten) {
        if (!ten) return false;
        return /^[A-Za-zÀ-ỹ0-9 ]+$/.test(ten.trim());
    }

    // Tìm kiếm: cho phép chữ/số/khoảng trắng/_/-
    function isValidSearchKeyword(text) {
        if (!text) return true; // cho phép rỗng
        return /^[A-Za-zÀ-ỹ0-9 _-]+$/.test(text.trim());
    }

    function isDuplicateMa(ma, currentId) {
        const m = (ma || "").trim().toLowerCase();
        if (!m) return false;
        return ($scope.allChatLieu || []).some(function (x) {
            return x.id !== currentId && (x.maCL || "").trim().toLowerCase() === m;
        });
    }

    function isDuplicateTen(ten, currentId) {
        const t = (ten || "").trim().toLowerCase();
        if (!t) return false;
        return ($scope.allChatLieu || []).some(function (x) {
            return x.id !== currentId && (x.tenCL || "").trim().toLowerCase() === t;
        });
    }

    function extractBackendMessage(err) {
        if (err && err.data) {
            if (typeof err.data === "string") return err.data;
            if (err.data.message) return err.data.message;
        }
        return "";
    }

    // ===== LOAD ALL =====
    $scope.load = function () {
        $http.get(API).then(function (res) {
            $scope.allChatLieu = res.data || [];
            $scope.applyFilterClient(); // lọc client sau khi load
        }).catch(function (err) {
            console.error("Lỗi load chất liệu", err);
            alert("Không load được danh sách chất liệu!");
        });
    };
    $scope.load();

    $scope.getSTT = function (i) { return i + 1; };

    // ===== FILTER (CLIENT CONTAINS) =====
    $scope.applyFilterClient = function () {
        const kw = ($scope.keyword || "").trim().toLowerCase();
        const tt = $scope.filterTrangThai;

        $scope.dsChatLieu = ($scope.allChatLieu || []).filter(function (x) {
            const ma = (x.maCL || "").toLowerCase();
            const ten = (x.tenCL || "").toLowerCase();

            const matchKw = !kw || ma.includes(kw) || ten.includes(kw);

            const matchTT =
                (tt === "" || tt === null || tt === undefined)
                    ? true
                    : Number(x.trangThai) === Number(tt);

            return matchKw && matchTT;
        });
    };

    // ===== SEARCH/FILTER =====
    $scope.keyword = "";
    $scope.filterTrangThai = "";
    $scope.searchSubmitted = false;
    $scope.searchErr = "";

    function clearSearchErr() {
        $scope.searchErr = "";
    }

// Tìm kiếm: cho phép chữ/số/khoảng trắng/_/-
    function isValidSearchKeyword(text) {
        if (!text) return true;
        return /^[A-Za-zÀ-ỹ0-9 _-]+$/.test(text.trim());
    }

// ✅ bấm nút Tìm mới validate bắt buộc nhập
    $scope.applyFilter = function () {
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();

        // ✅ validate chưa nhập gì
        if (!kw) {
            $scope.searchErr = "Vui lòng nhập nội dung tìm kiếm";
            return;
        }

        // ✅ validate ký tự đặc biệt
        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Tìm kiếm không được chứa ký tự đặc biệt";
            return;
        }

        $scope.applyFilterClient();
    };


    // realtime gõ -> tự lọc (debounce)
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
        // đổi trạng thái thì lọc luôn
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();
        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Tìm kiếm không được chứa ký tự đặc biệt";
            return;
        }
        $scope.applyFilterClient();
    };
    $scope.formatDateTime = function (val) {
        if (!val) return "";
        // nếu backend trả string ISO
        const d = new Date(val);
        if (!isNaN(d.getTime())) {
            const pad = (n) => (n < 10 ? "0" + n : "" + n);
            return (
                pad(d.getDate()) + "/" + pad(d.getMonth() + 1) + "/" + d.getFullYear() +
                " " + pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(d.getSeconds())
            );
        }

        // fallback nếu parse fail
        return val;
    };

    $scope.clearFilter = function () {
        $scope.keyword = "";
        $scope.filterTrangThai = "";
        $scope.searchSubmitted = false;
        clearSearchErr();
        $scope.dsChatLieu = angular.copy($scope.allChatLieu || []);
    };

    // ===== MODAL ADD/EDIT =====
    $scope.openAdd = function () {
        $scope.isEdit = false;
        $scope.submitted = false;
        clearErr();
        $scope.form = { trangThai: 1, maCL: "", tenCL: "", moTa: "" };
    };

    $scope.openEdit = function (cl) {
        $scope.isEdit = true;
        $scope.submitted = false;
        clearErr();
        $scope.form = angular.copy(cl);
        if ($scope.form.trangThai === undefined || $scope.form.trangThai === null) $scope.form.trangThai = 1;
    };

    $scope.resetForm = function (form) {
        $scope.submitted = false;
        clearErr();

        if (!$scope.isEdit) {
            $scope.form = { trangThai: 1, maCL: "", tenCL: "", moTa: "" };
        }

        if (form) {
            form.$setPristine();
            form.$setUntouched();
        }
    };

    // ===== SAVE =====
    $scope.save = function (form) {
        $scope.submitted = true;
        clearErr();

        if (form && form.$invalid) return;

        const ma = ($scope.form.maCL || "").trim();
        const ten = ($scope.form.tenCL || "").trim();

        if (!isValidMa(ma)) {
            $scope.err.maCL = "Mã chỉ gồm chữ/số/_/- và không có khoảng trắng";
            return;
        }
        if (!isValidTen(ten)) {
            $scope.err.tenCL = "Tên không được chứa ký tự đặc biệt";
            return;
        }

        if (isDuplicateMa(ma, $scope.isEdit ? $scope.form.id : null)) {
            $scope.err.maCL = "Mã chất liệu đã tồn tại";
            return;
        }
        if (isDuplicateTen(ten, $scope.isEdit ? $scope.form.id : null)) {
            $scope.err.tenCL = "Tên chất liệu đã tồn tại";
            return;
        }

        let payload = angular.copy($scope.form);
        payload.maCL = ma;
        payload.tenCL = ten;
        payload.trangThai = Number(payload.trangThai);

        if (!$scope.isEdit) {
            delete payload.id;
            $http.post(API, payload).then(function () {
                alert("Thêm chất liệu thành công!");
                $scope.load();
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalForm")).hide();
            }).catch(function (err) {
                console.error("Lỗi thêm", err);
                const msg = extractBackendMessage(err);
                if (msg.includes("Mã")) $scope.err.maCL = msg;
                else if (msg.includes("Tên")) $scope.err.tenCL = msg;
                else alert(msg || "Thêm thất bại!");
            });
        } else {
            if (!payload.id) { alert("Thiếu id để cập nhật!"); return; }
            $http.put(API + "/" + payload.id, payload).then(function () {
                alert("Cập nhật thành công!");
                $scope.load();
                bootstrap.Modal.getOrCreateInstance(document.getElementById("modalForm")).hide();
            }).catch(function (err) {
                console.error("Lỗi cập nhật", err);
                const msg = extractBackendMessage(err);
                if (msg.includes("Mã")) $scope.err.maCL = msg;
                else if (msg.includes("Tên")) $scope.err.tenCL = msg;
                else alert(msg || "Cập nhật thất bại!");
            });
        }
    };

    // ===== DETAIL =====
    $scope.showDetail = function (cl) {
        $scope.detail = null;
        if (!cl || !cl.id) return;

        $http.get(API + "/" + cl.id).then(function (res) {
            $scope.detail = res.data;
        }).catch(function () {
            $scope.detail = angular.copy(cl);
        });
    };

});
