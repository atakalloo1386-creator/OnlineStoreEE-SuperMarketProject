const field = (name, label, type = "text", options = {}) => ({ name, label, type, ...options });
const identifier = (name, label, required = true) => field(name, label, "number", { required, min: 1 });
const text = (name, label, maxLength, required = true) => field(name, label, "text", { required, maxLength });
const email = field("email", "ایمیل", "email", { required: true, maxLength: 100 });
const description = field("description", "توضیحات", "textarea", { maxLength: 500 });
const statusActions = [
    { label: "فعال‌سازی", path: "activate", method: "POST" },
    { label: "غیرفعال‌سازی", path: "deactivate", method: "POST" }
];
const financeNotice = "بک‌اند این بخش هنوز در حال تکمیل است. فعلاً نمایش لیست و ساختار فرم آماده است؛ عملیات نوشتن پس از تکمیل API توسط گروه مربوطه فعال می‌شود.";

export const groups = [
    ["catalog", "کاتالوگ"], ["users", "کاربران"], ["organization", "سازمان"],
    ["orders", "سفارش‌ها"], ["inventories", "انبار"], ["tickets", "پشتیبانی"],
    ["delivery", "ارسال"], ["financials", "مالی"], ["attachments", "پیوست‌ها"]
];

export const resources = [
    {
        id: "products", group: "catalog", title: "محصولات", path: "products", paged: true,
        fields: [text("name", "نام", 30), field("price", "قیمت", "number", { required: true, min: .01, step: "any" }),
            { ...description, maxLength: 200 }, field("active", "فعال", "boolean", { source: "isActive" }), identifier("categoryId", "شناسه دسته‌بندی")],
        filters: [identifier("categoryId", "شناسه دسته‌بندی", false)],
        columns: [["id", "شناسه"], ["name", "نام"], ["price", "قیمت"], ["categoryTitle", "دسته‌بندی"], ["isActive", "فعال"]]
    },
    {
        id: "categories", group: "catalog", title: "دسته‌بندی محصولات", path: "products/categories", paged: true,
        fields: [text("title", "عنوان", 30), field("description", "توضیحات", "textarea", { maxLength: 200 }),
            field("isActive", "فعال", "boolean", { source: "active" }), identifier("parentId", "شناسه دستهٔ والد", false)],
        columns: [["id", "شناسه"], ["title", "عنوان"], ["parentId", "والد"], ["active", "فعال"]],
        actions: [{ label: "زیرمجموعه‌ها", path: "sub-categories", method: "GET" }]
    },
    {
        id: "users", group: "users", title: "کاربران", path: "users", paged: true,
        description: "برای ویرایش کاربر، رمز عبور را وارد کنید. شناسهٔ نقش‌ها را با کاما جدا کنید.",
        fields: [text("username", "نام کاربری", 60), field("password", "رمز عبور", "password", { required: true, minLength: 8, maxLength: 72 }),
            field("enabled", "فعال", "boolean"), field("roleIds", "شناسهٔ نقش‌ها", "ids")],
        columns: [["id", "شناسه"], ["username", "نام کاربری"], ["enabled", "فعال"], ["roles", "نقش‌ها"]]
    },
    {
        id: "roles", group: "users", title: "نقش‌ها", path: "roles",
        fields: [text("name", "نام نقش", 50)], columns: [["id", "شناسه"], ["name", "نام نقش"]]
    },
    {
        id: "profiles", group: "users", title: "پروفایل کاربر", path: "users/{userId}/profile", single: true, saveMethod: "PUT", canDelete: false,
        scope: [identifier("userId", "شناسهٔ کاربر")],
        fields: [text("firstName", "نام", 30), text("lastName", "نام خانوادگی", 30), text("phoneNumber", "تلفن", 12, false),
            field("birthDate", "تاریخ تولد", "date"), text("address", "آدرس", 100, false)],
        columns: [["userId", "کاربر"], ["firstName", "نام"], ["lastName", "نام خانوادگی"], ["phoneNumber", "تلفن"]]
    },
    {
        id: "organizations", group: "organization", title: "سازمان‌ها", path: "organizations", paged: true,
        fields: [text("name", "نام", 150), text("registrationNumber", "شماره ثبت", 50), email,
            text("website", "وب‌سایت", 100, false), field("establishmentDate", "تاریخ تأسیس", "date")],
        columns: [["id", "شناسه"], ["name", "نام"], ["registrationNumber", "شماره ثبت"], ["email", "ایمیل"], ["status", "وضعیت"]],
        actions: [...statusActions, { label: "شعبه‌ها", path: "branches", method: "GET" }]
    },
    {
        id: "branches", group: "organization", title: "شعبه‌ها", path: "branches", paged: true,
        fields: [identifier("organizationId", "شناسهٔ سازمان"), text("name", "نام", 150), text("branchCode", "کد شعبه", 50),
            text("phoneNumber", "تلفن", 20, false), email, text("website", "وب‌سایت", 100, false),
            field("openingDate", "تاریخ افتتاح", "date"), text("address", "آدرس", 150)],
        filters: [identifier("organizationId", "شناسهٔ سازمان", false)],
        columns: [["id", "شناسه"], ["name", "نام"], ["branchCode", "کد"], ["organizationName", "سازمان"], ["status", "وضعیت"]],
        actions: [...statusActions, { label: "دپارتمان‌ها", path: "departments", method: "GET" }]
    },
    {
        id: "departments", group: "organization", title: "دپارتمان‌ها", path: "departments", paged: true,
        fields: [identifier("branchId", "شناسهٔ شعبه"), text("name", "نام", 150), text("code", "کد", 50), description],
        filters: [identifier("branchId", "شناسهٔ شعبه", false), identifier("userId", "شناسهٔ کارمند", false)],
        columns: [["id", "شناسه"], ["name", "نام"], ["code", "کد"], ["branchName", "شعبه"], ["status", "وضعیت"]],
        actions: [...statusActions,
            { label: "کارکنان", path: "employees", method: "GET" },
            { label: "افزودن کارمند", path: "employees/{userId}", method: "PUT", input: { name: "userId", label: "شناسهٔ کارمند" } },
            { label: "حذف کارمند", path: "employees/{userId}", method: "DELETE", input: { name: "userId", label: "شناسهٔ کارمند" } }
        ]
    },
    {
        id: "orders", group: "orders", title: "سفارش‌ها", path: "orders", paged: true,
        description: "اقلام سفارش به شکل JSON وارد می‌شوند؛ شناسهٔ محصول، تعداد و قیمت واحد را مشخص کنید.",
        fields: [field("customer", "مشتری", "json", { required: true, default: { id: 1 }, help: 'نمونه: {"id":1}' }),
            field("orderItems", "اقلام سفارش", "json", { required: true, default: [{ product: { id: 1 }, quantity: 1, unitPrice: 100 }] }),
            field("shippingMethod", "روش ارسال", "text", { required: true, options: ["POST", "PICKUP", "COURIER"] }),
            field("shippingStatus", "وضعیت ارسال", "text", { required: true, options: ["PENDING", "SHIPPED", "DELIVERED"] }),
            field("total", "مبلغ کل", "number", { required: true, min: 0, step: "any" }),
            field("orderType", "نوع سفارش", "text", { required: true, options: ["BUY", "SELL", "CART"] }),
            field("orderStatus", "وضعیت سفارش", "text", { required: true, options: ["NEW", "CONFIRMED", "CANCELLED"] })],
        filters: [identifier("customerId", "شناسهٔ مشتری", false)], filterPath: "orders/customer/{customerId}",
        columns: [["id", "شناسه"], ["customer", "مشتری"], ["total", "مبلغ"], ["orderStatus", "وضعیت"], ["shippingStatus", "ارسال"]],
        actions: [
            { label: "تأیید", path: "confirm", method: "PUT" }, { label: "لغو", path: "cancel", method: "PUT" },
            { label: "ارسال", path: "ship", method: "PUT" }, { label: "تحویل", path: "deliver", method: "PUT" }
        ]
    },
    {
        id: "inventories", group: "inventories", title: "انبارها", path: "inventories", paged: true,
        fields: [text("name", "نام", 60), text("location", "محل", 60)], columns: [["id", "شناسه"], ["name", "نام"], ["location", "محل"]]
    },
    {
        id: "inventory-items", group: "inventories", title: "موجودی کالا", path: "inventory-items", paged: true,
        fields: [identifier("inventoryId", "شناسهٔ انبار"), identifier("productId", "شناسهٔ محصول"),
            field("minimumQuantity", "حداقل موجودی", "number", { required: true, min: 0 }), text("batchNumber", "شماره بچ", 60), field("expirationDate", "تاریخ انقضا", "date")],
        columns: [["id", "شناسه"], ["productName", "محصول"], ["inventoryId", "انبار"], ["quantity", "موجودی"], ["minimumQuantity", "حداقل"]],
        extraLists: [{ label: "کمبود موجودی", path: "inventory-items/low-stock" }]
    },
    {
        id: "inventory-transactions", group: "inventories", title: "گردش موجودی", path: "inventory-items/{itemId}/transactions", canEdit: false, canDelete: false,
        scope: [identifier("itemId", "شناسهٔ موجودی کالا")],
        fields: [field("quantity", "تعداد", "number", { required: true }),
            field("type", "نوع", "text", { required: true, options: ["IN", "OUT", "RETURN", "ADJUSTMENT"] }),
            field("referenceType", "مرجع", "text", { required: true, options: ["ORDER", "MANUAL"] }),
            identifier("referenceId", "شناسهٔ مرجع", false), text("reason", "علت", 150)],
        columns: [["id", "شناسه"], ["quantity", "تعداد"], ["type", "نوع"], ["referenceType", "مرجع"], ["reason", "علت"]]
    },
    {
        id: "tickets", group: "tickets", title: "تیکت‌ها", path: "tickets",
        fields: [text("subject", "موضوع", 150), text("username", "نام کاربری", 60), { ...identifier("userId", "شناسهٔ کاربر"), source: "userID" },
            field("status", "وضعیت", "text", { required: true, source: "ticketStatus", options: ["OPEN", "IN_PROGRESS", "CLOSED"] }),
            field("createdAt", "زمان ایجاد", "datetime-local"), field("updatedAt", "زمان ویرایش", "datetime-local")],
        columns: [["id", "شناسه"], ["subject", "موضوع"], ["username", "کاربر"], ["ticketStatus", "وضعیت"]]
    },
    {
        id: "ticket-messages", group: "tickets", title: "پیام‌های تیکت", path: "tickets/{ticketId}/messages", canEdit: false, canDelete: false,
        scope: [identifier("ticketId", "شناسهٔ تیکت")],
        fields: [identifier("senderId", "شناسهٔ فرستنده"), field("content", "متن پیام", "textarea", { required: true, maxLength: 2000 }), field("createdAt", "زمان ایجاد", "datetime-local")],
        columns: [["id", "شناسه"], ["senderUsername", "فرستنده"], ["content", "متن"], ["createdAt", "زمان"]]
    },
    {
        id: "couriers", group: "delivery", title: "پیک‌ها", path: "couriers", paged: true,
        fields: [text("name", "نام", 50), text("phone", "تلفن", 15), field("email", "ایمیل", "email"), text("vehicleType", "نوع وسیله", 200)],
        columns: [["id", "شناسه"], ["name", "نام"], ["phone", "تلفن"], ["vehicleType", "وسیله"]]
    },
    {
        id: "deliveries", group: "delivery", title: "مرسوله‌ها", path: "deliveries", paged: true,
        fields: [field("address", "آدرس", "text", { required: true, minLength: 30, maxLength: 300 }),
            text("trackingNumber", "شمارهٔ پیگیری", 60), field("deliveryNote", "یادداشت", "textarea", { maxLength: 500 }), identifier("courierId", "شناسهٔ پیک")],
        filters: [identifier("courierId", "شناسهٔ پیک", false)],
        columns: [["id", "شناسه"], ["trackingNumber", "پیگیری"], ["courierName", "پیک"], ["status", "وضعیت"], ["address", "آدرس"]],
        actions: [{ label: "ارسال", path: "send", method: "POST" }, { label: "تحویل", path: "confirm", method: "POST" }]
    },
    {
        id: "banks", group: "financials", title: "بانک‌ها", path: "banks", readOnly: true, notice: financeNotice,
        fields: [text("bankName", "نام بانک"), text("bankCode", "کد بانک")], columns: [["id", "شناسه"], ["bankName", "بانک"], ["bankCode", "کد"]]
    },
    {
        id: "bankaccounts", group: "financials", title: "حساب‌های بانکی", path: "bankaccounts", readOnly: true, notice: financeNotice,
        fields: [identifier("bankid", "شناسهٔ بانک"), text("accountNumber", "شماره حساب"), text("ownerName", "نام صاحب حساب")],
        columns: [["id", "شناسه"], ["bankName", "بانک"], ["accountNumber", "شماره حساب"], ["ownerName", "صاحب حساب"], ["balance", "مانده"]]
    },
    {
        id: "cashiers", group: "financials", title: "صندوق‌داران", path: "cashiers", readOnly: true, notice: financeNotice,
        fields: [text("cashierName", "نام"), text("employeeCode", "کد کارمند", 60, false)], columns: [["id", "شناسه"], ["cashierName", "نام"], ["employeeCode", "کد کارمند"]]
    },
    {
        id: "payments", group: "financials", title: "پرداخت‌ها", path: "payments", readOnly: true, notice: financeNotice,
        fields: [identifier("amount", "مبلغ"), description], columns: [["id", "شناسه"], ["orderId", "سفارش"], ["amount", "مبلغ"], ["type", "نوع"], ["status", "وضعیت"]]
    },
    {
        id: "card-payments", group: "financials", title: "پرداخت کارتی", path: "payments/cards", readOnly: true, notice: financeNotice,
        fields: [identifier("cardId", "شناسهٔ کارت"), text("cardLastDigits", "چهار رقم آخر", 4), text("trackingCode", "کد پیگیری")],
        columns: [["id", "شناسه"], ["cardLastDigits", "چهار رقم آخر"], ["trackingCode", "پیگیری"]]
    },
    {
        id: "cash-payments", group: "financials", title: "پرداخت نقدی", path: "payments/cashs", readOnly: true, notice: financeNotice,
        fields: [identifier("cashierId", "صندوق‌دار"), identifier("orderId", "سفارش"), field("date", "تاریخ", "date"), identifier("amount", "مبلغ")],
        columns: [["id", "شناسه"], ["orderId", "سفارش"], ["cashierId", "صندوق‌دار"], ["amount", "مبلغ"], ["date", "تاریخ"]]
    },
    {
        id: "cheque-payments", group: "financials", title: "پرداخت چکی", path: "payments/cheques", readOnly: true, notice: financeNotice,
        fields: [identifier("orderId", "سفارش"), identifier("amount", "مبلغ"), identifier("bankId", "بانک"), text("chequeNumber", "شماره چک"), field("dueDate", "سررسید", "datetime-local")],
        columns: [["id", "شناسه"], ["orderId", "سفارش"], ["chequeNumber", "شماره چک"], ["amount", "مبلغ"], ["dueDate", "سررسید"]]
    },
    {
        id: "transactions", group: "financials", title: "تراکنش‌های مالی", path: "transactions", readOnly: true, notice: financeNotice,
        fields: [identifier("cashierId", "صندوق‌دار"), field("type", "نوع", "text", { required: true, options: ["DEPOSIT", "WITHDRAW"] }),
            field("date", "تاریخ", "datetime-local"), identifier("amount", "مبلغ"), { ...description, required: true }],
        columns: [["id", "شناسه"], ["cashierId", "صندوق‌دار"], ["type", "نوع"], ["amount", "مبلغ"], ["description", "توضیحات"]]
    },
    {
        id: "attachments", group: "attachments", title: "پیوست‌ها", path: "attachments", paged: true, files: true,
        description: "ابتدا متادیتا را ذخیره کنید، سپس فایل را برای همان شناسه آپلود کنید.",
        fields: [field("ownerType", "نوع مالک", "text", { required: true, options: ["PRODUCT", "USER", "ORGANIZATION", "BRANCH", "TICKET", "TICKET_MESSAGE", "PAYMENT", "ORDER"] }),
            identifier("ownerId", "شناسهٔ مالک"), text("title", "عنوان", 100), text("fileName", "نام فایل", 255), description, text("keywords", "کلیدواژه‌ها", 500, false)],
        filters: [field("ownerType", "نوع مالک", "text", { options: ["PRODUCT", "USER", "ORGANIZATION", "BRANCH", "TICKET", "TICKET_MESSAGE", "PAYMENT", "ORDER"] }), identifier("ownerId", "شناسهٔ مالک", false)],
        columns: [["id", "شناسه"], ["title", "عنوان"], ["ownerType", "مالک"], ["ownerId", "شناسه مالک"], ["fileName", "نام فایل"], ["fileSize", "حجم (بایت)"]]
    }
];
