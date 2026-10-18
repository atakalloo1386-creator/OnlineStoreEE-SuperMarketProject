<div dir="rtl">

# OpenAPI سادهٔ Attachment

قرارداد API پیوست در یک فایل ثابت OpenAPI 3.0.3 نوشته شده است.
برای این پروژهٔ آموزشی generator، annotation اضافی یا وابستگی Java اضافه نشده است.

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| سند ثابت OpenAPI | انجام‌شده | attachments-openapi.json |
| مستندسازی metadata و paging | انجام‌شده | collection، /page و /{id} |
| مستندسازی raw upload/download | انجام‌شده | PUT/GET /{id}/content |
| Request، DTO، envelope و خطاها | انجام‌شده | schemaها و نمونه‌ها |
| محدودیت‌های ورودی و نوع مالک | انجام‌شده | مطابق DTO فعلی و enumهای Attachment |
| بیان HTTP Basic و دسترسی موردنظر | انجام‌شده | Security در عملیات محافظت‌شده |
| Swagger UI مستقل از UI اصلی | انجام‌شده | index.html و swagger.js؛ bundle فقط در docs |
| نگه‌داری‌نکردن authorization در Swagger | انجام‌شده | persistAuthorization=false |
| validator خارجی | خارج از دامنه | validatorUrl=null؛ داده برای سرویس validator ارسال نمی‌شود |
| اجرای authentication/authorization server | انجام‌نشده | زیرساخت امنیت backend هنوز کامل نیست |
| بررسی عملی تطابق قرارداد با HTTP | انجام‌نشده | در این نوبت deploy/Swagger Try it out اجرا نشده است |
| تست خودکار قرارداد OpenAPI | انجام‌نشده | تست اختصاصی وجود ندارد |
| همگام‌سازی پس از تغییر API/DTO | باقی‌ماندهٔ مستمر | سند ثابت است و خودکار از Java تولید نمی‌شود |

«انجام‌شده» یعنی مستند یا صفحه موجود است؛ package صحت schema و
رفتار endpoint را به‌صورت خودکار تأیید نمی‌کند.

## دسترسی

- Swagger UI: `/docs/index.html`
- فایل قرارداد: `/docs/attachments-openapi.json`
- server نسبی در سند: `../api/v1` نسبت به محل فایل OpenAPI

در استقرار غیر ROOT، context path به اول مسیرها اضافه می‌شود.
Swagger UI از CDN نسخه‌دار دریافت می‌شود و اینترنت لازم دارد.
برای دیدن سند خام به JavaScript نیازی نیست.

## عملیات مستندشده

| HTTP | مسیر پس از /api/v1 |
|---|---|
| GET / POST | `/attachments` |
| GET | `/attachments/page` |
| GET / PUT / DELETE | `/attachments/{id}` |
| GET / PUT | `/attachments/{id}/content` |

جمعاً ۸ عملیات در ۴ مسیر مستند شده است.
body آپلود فایل خام است، نه multipart. metadata ابتدا جداگانه ساخته می‌شود.
پاسخ metadata داخل ApiResponse است؛ stream دانلود و 204 پوشش داده نمی‌شوند.

## نگه‌داری سند

هنگام تغییر `AttachmentApi`، Request/Dto، owner enum یا قالب خطا:
مسیر، method، پارامترها، schema و پاسخ‌ها را همین‌جا به‌روز کنید.
بیان admin یا attachment:file در سند، فقط قرارداد دسترسی است؛
وجود آن اجرای واقعی مجوز را در server ثابت نمی‌کند.

گسترش OpenAPI به ماژول‌های دانشجویی یا تولید خودکار سند در این تسک لازم نیست.
[README پیوست](../../java/com/mftplus/mftonlinestoreee/attachments/readme.md)
جزئیات پیاده‌سازی و تست‌ها را دارد.

## نتیجهٔ بررسی

در ۲۰۲۶/۱۰/۱۳، package موفق و فایل‌ها داخل WAR قرار گرفتند.
۳۰ تست Attachment موفق‌اند، اما تست‌های مستقیم آن schema، Swagger UI و
مجوز HTTP را بررسی نمی‌کنند. در این نوبت فقط README اضافه شده است.

</div>
