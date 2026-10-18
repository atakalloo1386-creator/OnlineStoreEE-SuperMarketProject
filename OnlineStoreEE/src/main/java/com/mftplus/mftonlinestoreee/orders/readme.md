<div dir="rtl">

# سفارش و قلم‌های سفارش

مسئولیت: سفارش مشتری، اقلام، مبلغ و وضعیت ارسال.
وابسته به User و Product است؛ entity فعلی Order به Payment هم رابطهٔ اجباری دارد.
الگو: [Attachment](../attachments/readme.md) و [راهنمای دانشجو](../student-guide.md).

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entity سفارش و قلم‌ها | انجام‌شده | cascade/orphanRemoval، رابطهٔ customer/product، quantity و unitPrice |
| وضعیت‌های سفارش و ارسال | انجام‌شده | enumها و مقدار اولیه در create سرویس |
| repository و جست‌وجوی مشتری/وضعیت | انجام‌شده | OrderRepository |
| مبلغ BigDecimal | انجام‌شده | total و unitPrice؛ precision/scale و کنترل سمت سرور هنوز لازم است |
| DTO بدون entity | انجام‌نشده | OrderRequest/OrderDto هنوز User و List<OrderItem> دارند |
| Request امن اقلام با productId و quantity | انجام‌نشده | OrderItemRequest هم Order/Product و unitPrice می‌گیرد؛ request سفارش از آن استفاده نمی‌کند |
| mapping رابطه‌های خروجی و فیلدهای server-owned | ناقص | OrderMapper فقط BaseMapper است و entityها/مبلغ/وضعیت را از request می‌گیرد |
| CRUD و paging اولیه | انجام‌شده | متدهای service و مسیرهای API وجود دارند؛ منطق کامل نیست |
| مشتری موجود و سفارش قابل ویرایش | انجام‌شده | loadCustomer و checkEditable |
| ساخت قلم‌ها و اتصال order/product | انجام‌نشده | `buildItems` خالی است و در create/update فراخوانی نمی‌شود |
| محصول موجود/فعال و محصول تکراری | انجام‌نشده | فرایند ساخت اقلام و قواعد آن تکمیل نشده است |
| محاسبهٔ total در create/update | انجام‌نشده | calculateTotal موجود است، ولی فراخوانی و روی order اعمال نمی‌شود |
| تأیید و لغو سفارش NEW | انجام‌شده | متدهای confirm/cancel و API اختصاصی وجود دارند؛ تست ندارند |
| ارسال و تحویل | ناقص | ship/deliver، update را صدا می‌زنند؛ checkEditable سفارش CONFIRMED را رد می‌کند |
| رابطهٔ پرداخت در ایجاد سفارش | انجام‌نشده | paymentId در entity اجباری است، ولی Request و create آن را تعیین نمی‌کنند |
| transaction در خواندن relationها | ناقص | خواندن‌های service Transactional نیستند |
| مسیرهای بدون تداخل | انجام‌شده | customer جدا و عملیات وضعیت دارای suffix است |
| UI سفارش | انجام‌شده | فرم JSON متناسب با request فعلی؛ قرارداد ایمن نهایی هنوز لازم است |
| تست مبلغ، اقلام و انتقال وضعیت | انجام‌نشده | هیچ تست orders اجرا نشده است |
| پاک‌سازی کد و بررسی HTTP | ناقص | servletهای قدیمی و helper خالی باقی‌اند؛ deploy در این نوبت اجرا نشد |

«انجام‌شده» در این جدول وجود کد است، نه تأیید صحت سفارش از ابتدا تا انتها.

## مسیرهای فعلی

همه پس از `/api/v1` قرار می‌گیرند.

| HTTP | مسیر |
|---|---|
| GET / POST | `/orders` |
| GET | `/orders/page` |
| GET | `/orders/customer/{id}` |
| GET / PUT / DELETE | `/orders/{id}` |
| PUT | `/orders/{id}/confirm` |
| PUT | `/orders/{id}/cancel` |
| PUT | `/orders/{id}/ship` |
| PUT | `/orders/{id}/deliver` |

عملیات وضعیت فعلاً PUT است، نه POST پیشنهادی دستور کار قبلی.

## ترتیب ادامه

۱. OrderRequest را به customerId، shippingMethod و List<OrderItemRequest> محدود کنید.
DTO خروجی نیز به شناسه‌ها و OrderItemDto تبدیل شود؛ entity User ممکن است hash
رمز و داده‌های داخلی را در JSON منتشر کند.
۲. customer و productهای واقعی را در service بارگذاری کنید؛ unitPrice از
قیمت محصول و total از جمع quantity × unitPrice ساخته شود.
۳. به هر قلم، order اختصاص دهید و قواعد محصول تکراری/غیرفعال را اجرا کنید.
۴. رابطهٔ اجباری پرداخت را با چرخهٔ ایجاد سفارش سازگار کنید.
۵. انتقال وضعیت را مستقل از update عمومی پیاده کنید؛ ship برای
CONFIRMED/PENDING و deliver فقط برای SHIPPED باشد.
۶. mapping Lazy، تست‌ها و قرارداد UI را تکمیل کنید.

## نتیجهٔ بررسی

کامپایل و package با `-DskipTests` در ۲۰۲۶/۱۰/۱۳ موفق شد،
اما تست orders وجود ندارد و ساخت WAR منطق مبلغ/پرداخت/ارسال را تأیید نمی‌کند.
تمام کارهای باقی‌مانده فقط مستند شده‌اند؛ کد تغییر نکرده است.

</div>
