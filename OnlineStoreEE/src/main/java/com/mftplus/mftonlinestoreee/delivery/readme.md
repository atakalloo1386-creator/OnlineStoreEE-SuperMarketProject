<div dir="rtl">

# پیک و مرسوله

مسئولیت: اطلاعات پیک، ثبت مرسوله و ثبت زمان ارسال/تحویل.
این README وضعیت کد موجود را ثبت می‌کند؛ سرویس ارسال واقعی بیرونی در دامنهٔ کلاس نیست.

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entityهای Courier و Delivery | انجام‌شده | BaseEntity، soft delete و رابطهٔ پیک |
| repository، DTO و mapper | انجام‌شده | جست‌وجوی پیک/شمارهٔ پیگیری و mapping شناسه/نام پیک |
| CRUD و paging پیک و مرسوله | انجام‌شده | serviceها و CourierApi/DeliveryApi |
| پیک ناموجود و شمارهٔ پیگیری تکراری | انجام‌شده | کنترل service و BusinessException |
| فیلتر مرسوله‌های پیک | انجام‌شده | GET با courierId |
| ثبت ارسال و تحویل | انجام‌شده | send/confirmDelivery وضعیت و زمان را تغییر می‌دهند |
| مقدار اولیهٔ PREPARING هنگام builder | ناقص | هشدار کامپایل: مقدار اولیهٔ status بدون Builder.Default نادیده گرفته می‌شود |
| کنترل ترتیب و تکرار انتقال وضعیت | انجام‌نشده | entity.send/confirmDelivery بدون guard وضعیت را تغییر می‌دهند |
| transaction خواندن relation Lazy | انجام‌شده | خواندن‌های DeliveryServiceImpl دارای Transactional هستند |
| UI پیک و مرسوله | انجام‌شده | CRUD، فیلتر و دکمه‌های ارسال/تحویل |
| بررسی یکتایی trackingNumber پس از soft delete | انجام‌نشده | constraint database مقدار رکورد حذف‌شده را همچنان نگه می‌دارد |
| پاک‌سازی servlet و کد کامنت‌شده | ناقص | اسکلت‌ها و کد قدیمی باقی‌اند |
| تست CRUD و انتقال وضعیت | انجام‌نشده | تست خودکار delivery وجود ندارد |
| بررسی API روی TomEE و UI | انجام‌نشده | در این نوبت deploy/مرورگر اجرا نشد |

«انجام‌شده» یعنی کد مربوط موجود است؛ صحت یکپارچهٔ عملیات هنوز تأیید نشده است.

## مسیرهای فعلی

همه پس از `/api/v1` هستند.

| مسیر | عملیات |
|---|---|
| `/couriers` و `/couriers/{id}` | CRUD |
| `/couriers/page` | GET صفحه |
| `/deliveries` و `/deliveries/{id}` | CRUD |
| `/deliveries/page` | GET صفحه |
| `/deliveries?courierId=1` | GET مرسوله‌های پیک |
| `/deliveries/{id}/send` | POST ارسال |
| `/deliveries/{id}/confirm` | POST تحویل |

DeliveryRequest شامل address، trackingNumber، deliveryNote و courierId است.
وضعیت و زمان‌ها باید از server تعیین شوند.

## ترتیب ادامه

۱. مقدار اولیهٔ status در builder را درست و سپس guard انتقال وضعیت را اضافه کنید.
۲. سناریوی ثبت شمارهٔ پیگیری پس از حذف نرم را بررسی کنید.
۳. تست service/API بنویسید و CRUD و عملیات وضعیت را در مرورگر بررسی کنید.

## نتیجهٔ بررسی

در ۲۰۲۶/۱۰/۱۳ کامپایل و package با `-DskipTests` موفق شد،
ولی هشدار status باقی ماند. هیچ تست delivery اجرا نشده است.
برای رعایت درخواست، هشدار یا کد این بخش اصلاح نشده؛ فقط README اضافه شده است.

</div>
