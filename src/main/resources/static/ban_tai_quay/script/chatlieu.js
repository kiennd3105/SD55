// chatlieu.js
angular.module("myApp").controller("chatLieuCtrl", function ($scope, $http) {

    const API = "http://localhost:8084/san-pham/chat-lieu";

    // ====== STATE ======
    $scope.dsChatLieu = [];
    $scope.form = { trangThai: 1 };
    $scope.isEdit = false;
    $scope.submitted = false;

    // FILTER
    $scope.keyword = "";
    $scope.filterTrangThai = ""; // ""=tất cả, "1"=hoạt động, "0"=ngừng
    $scope.searchSubmitted = false; // validate riêng cho tìm kiếm
    $scope.searchErr = "";          // lỗi tìm kiếm

    // DETAIL
    $scope.detail = null;

    // ERROR MESSAGE FORM (hiện dưới input)
    $scope.err = { maCL: "", tenCL: "" };

    // ====== helpers ======
    function clearFormErr() {
        $scope.err.maCL = "";
        $scope.err.tenCL = "";
    }

    function clearSearchErr() {
        $scope.searchErr = "";
    }

    // Mã: chỉ chữ/số/_/- , KHÔNG khoảng trắng
    function isValidMa(ma) {
        if (!ma) return false;
        return /^[A-Za-z0-9_-]+$/.test(ma.trim());
    }

    // Tên: chữ có dấu + số + khoảng trắng
    function isValidTen(ten) {
        if (!ten) return false;
        return /^[A-Za-zÀ-ỹ0-9 ]+$/.test(ten.trim());
    }

    // Tìm kiếm: cho phép chữ có dấu + số + khoảng trắng + _ - (không ký tự đặc biệt)
    function isValidSearchKeyword(kw) {
        if (!kw) return true; // rỗng thì coi như hợp lệ
        return /^[A-Za-zÀ-ỹ0-9 _-]+$/.test(kw.trim());
    }

    function isDuplicateMa(ma, currentId) {
        const m = (ma || "").trim().toLowerCase();
        if (!m) return false;
        return ($scope.dsChatLieu || []).some(function (x) {
            return x.id !== currentId && (x.maCL || "").trim().toLowerCase() === m;
        });
    }

    function isDuplicateTen(ten, currentId) {
        const t = (ten || "").trim().toLowerCase();
        if (!t) return false;
        return ($scope.dsChatLieu || []).some(function (x) {
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

    // ====== LOAD (có keyword + trangThai) ======
    $scope.load = function () {
        const params = {};

        if ($scope.keyword && $scope.keyword.trim()) params.keyword = $scope.keyword.trim();

        if ($scope.filterTrangThai !== "" && $scope.filterTrangThai !== null && $scope.filterTrangThai !== undefined) {
            params.trangThai = Number($scope.filterTrangThai);
        }

        $http.get(API, { params: params })
            .then(function (res) {
                $scope.dsChatLieu = res.data || [];
            })
            .catch(function (err) {
                console.error("Lỗi load chất liệu", err);
                alert("Không load được danh sách chất liệu!");
            });
    };

    // load lần đầu
    $scope.load();

    $scope.getSTT = function (i) { return i + 1; };

    // ====== FILTER ACTIONS ======
    $scope.applyFilter = function () {
        $scope.searchSubmitted = true;
        clearSearchErr();

        const kw = ($scope.keyword || "").trim();

        // validate keyword (không bắt buộc nhập)
        if (!isValidSearchKeyword(kw)) {
            $scope.searchErr = "Từ khóa chỉ được gồm chữ/số/khoảng trắng/_/- (không ký tự đặc biệt)";
            return;
        }

        $scope.load();
    };

    $scope.clearFilter = function () {
        $scope.keyword = "";
        $scope.filterTrangThai = "";
        $scope.searchSubmitted = false;
        clearSearchErr();
        $scope.load();
    };

    // ====== DETAIL ======
    $scope.showDetail = function (cl) {
        $scope.detail = null;
        if (!cl || !cl.id) return;

        // Nếu backend bạn CHƯA có GET /{id} thì phần này sẽ fail -> fallback show từ list
        $http.get(API + "/" + cl.id)
            .then(function (res) {
                $scope.detail = res.data;
            })
            .catch(function () {
                $scope.detail = angular.copy(cl);
            });
    };

    // ====== EDIT ======
    $scope.edit = function (cl) {
        clearFormErr();
        $scope.form = angular.copy(cl);
        if ($scope.form.trangThai === undefined || $scope.form.trangThai === null) $scope.form.trangThai = 1;
        $scope.isEdit = true;
        $scope.submitted = false;
    };

    // ====== RESET ======
    $scope.reset = function (form) {
        $scope.form = { trangThai: 1 };
        $scope.isEdit = false;
        $scope.submitted = false;
        clearFormErr();

        if (form) {
            form.$setPristine();
            form.$setUntouched();
        }
    };

    // ====== ADD ======
    $scope.add = function (form) {
        $scope.submitted = true;
        clearFormErr();

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

        if (isDuplicateMa(ma, null)) {
            $scope.err.maCL = "Mã chất liệu đã tồn tại";
            return;
        }
        if (isDuplicateTen(ten, null)) {
            $scope.err.tenCL = "Tên chất liệu đã tồn tại";
            return;
        }

        let payload = angular.copy($scope.form);
        delete payload.id;
        payload.maCL = ma;
        payload.tenCL = ten;
        payload.trangThai = Number(payload.trangThai);

        $http.post(API, payload)
            .then(function () {
                alert("Thêm chất liệu thành công!");
                $scope.load();
                $scope.reset(form);
            })
            .catch(function (err) {
                console.error("Lỗi thêm chất liệu", err);
                const msg = extractBackendMessage(err);
                if (msg.includes("Mã")) $scope.err.maCL = msg;
                else if (msg.includes("Tên")) $scope.err.tenCL = msg;
                else alert(msg || "Thêm thất bại!");
            });
    };

    // ====== UPDATE ======
    $scope.update = function (form) {
        $scope.submitted = true;
        clearFormErr();

        if (form && form.$invalid) return;

        if (!$scope.form.id) {
            alert("Thiếu id để cập nhật!");
            return;
        }

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

        if (isDuplicateMa(ma, $scope.form.id)) {
            $scope.err.maCL = "Mã chất liệu đã tồn tại";
            return;
        }
        if (isDuplicateTen(ten, $scope.form.id)) {
            $scope.err.tenCL = "Tên chất liệu đã tồn tại";
            return;
        }

        let payload = angular.copy($scope.form);
        payload.maCL = ma;
        payload.tenCL = ten;
        payload.trangThai = Number(payload.trangThai);

        $http.put(API + "/" + payload.id, payload)
            .then(function () {
                alert("Cập nhật thành công!");
                $scope.load();
                $scope.reset(form);
            })
            .catch(function (err) {
                console.error("Lỗi cập nhật chất liệu", err);
                const msg = extractBackendMessage(err);
                if (msg.includes("Mã")) $scope.err.maCL = msg;
                else if (msg.includes("Tên")) $scope.err.tenCL = msg;
                else alert(msg || "Cập nhật thất bại!");
            });
    };

    // ====== DELETE (frontend gọi API DELETE /{id}) ======
    $scope.remove = function (cl) {
        if (!cl || !cl.id) return;
        if (!confirm("Bạn có chắc muốn xóa chất liệu '" + (cl.tenCL || "") + "' không?")) return;

        $http.delete(API + "/" + cl.id)
            .then(function () {
                alert("Xóa thành công!");
                $scope.load();
                // nếu đang sửa đúng bản ghi đó thì reset
                if ($scope.form && $scope.form.id === cl.id) $scope.reset($scope.clForm);
            })
            .catch(function (err) {
                console.error("Lỗi xóa chất liệu", err);
                alert("Xóa thất bại! Có thể đang được dùng ở bảng khác.");
            });
    };

});
