<div dir="rtl">

# فروشگاه آموزشی Jakarta EE

هدف پروژه تمرین معماری سادهٔ `REST → Service → Repository → Database` است.
زیرساخت، تنظیمات و ماژول مرجع Attachment با مدرس است؛ تکمیل ماژول‌های کسب‌وکار با دانشجویان.

## راهنمای وضعیت‌ها

- **انجام‌شده:** کد یا مستند مربوط وجود دارد؛ لزوماً به معنی تأیید اجرای آن روی سرور نیست.
- **ناقص:** بخشی پیاده شده، اما موارد مشخصی باقی مانده است.
- **انجام‌نشده:** پیاده‌سازی یا بررسی موردنظر هنوز وجود ندارد.
- نتیجهٔ تست و build جدا از وضعیت پیاده‌سازی ثبت شده است.

## جدول تسک‌های پروژه

| بخش / تسک | وضعیت | انجام‌شده | کار باقی‌مانده |
|---|---|---|---|
| Core و تنظیمات | انجام‌شده | کلاس‌های پایه، JPA/JTA، Hikari، پاسخ و خطای مشترک، logging | بررسی یکپارچهٔ نسخهٔ فعلی روی TomEE |
| Docker | انجام‌شده | JDK 17، TomEE 10.2.0، PostgreSQL 18.6، Redis 8.8.2، volume و healthcheck | در این نوبت Docker اجرا نشده است |
| Attachment | انجام‌شده | CRUD، paging، مالک، آپلود و دانلود؛ ۳۰ تست موفق | بررسی HTTP روی سرور و اجرای واقعی مجوزها |
| امنیت | ناقص | BCrypt و annotationهای دسترسی؛ فایل SQL نمونه | تنظیم authentication، اجرای authorization و فعال‌سازی seed |
| کاربران | ناقص | CRUD کاربر، hash رمز، تخصیص نقش؛ ساخت و خواندن نقش | سرویس پروفایل، ویرایش/حذف نقش و قواعد نقش |
| محصولات | ناقص | CRUD، دسته‌بندی، جست‌وجو و قواعد پایه | اصلاح mapping وضعیت فعال، validation عنوان و خواندن Lazy؛ تست واقعی |
| سازمان / شعبه / دپارتمان | انجام‌شده | CRUD، وضعیت، قواعد وابستگی و کارمندان دپارتمان | تست خودکار و بررسی endpointها روی سرور |
| کش Redis | انجام‌شده | کش DTO سازمان و دپارتمان با TTL و ابطال پس از commit | تست hit/miss، rollback و قطعی Redis |
| سفارش | ناقص | entity، repository، CRUD و مسیرهای عملیات وضعیت | DTO بدون entity، ساخت قلم‌ها، محاسبهٔ مبلغ و اصلاح ارسال/تحویل |
| انبار | ناقص | CRUD، موجودی و گردش کالا با قواعد کسب‌وکار | اصلاح تنظیم testPU، تکمیل تست‌های خالی و بررسی خواندن Lazy |
| تیکت | ناقص | CRUD، سرویس بستن و API پیام‌های تو‌در‌تو | API بستن، paging و فیلتر کاربر؛ ساده‌سازی DTO و سرویس پیام |
| ارسال | ناقص | CRUD پیک و مرسوله، API ارسال/تحویل و فیلتر پیک | مقدار اولیهٔ وضعیت در builder، کنترل انتقال وضعیت و تست |
| مالی | ناقص | ساختار entity/repository/DTO/mapper و APIهای خواندن اولیه | متدهای خالی، قواعد مالی، CDI، قرارداد DTO و APIهای نوشتن |
| OpenAPI پیوست | انجام‌شده | سند ثابت OpenAPI 3.0.3 و Swagger UI | بررسی عملی قرارداد و مجوزها روی سرور |
| UI ساده | انجام‌شده | HTML، Bootstrap RTL، JavaScript و fetch مشترک؛ ۲۵ صفحه | بررسی در مرورگر؛ تکمیل backend صفحات ناقص |
| عملیات نوشتن UI مالی | انجام‌نشده | نمایش ساختار فرم به‌صورت غیرفعال | پس از تکمیل سرویس و API مالی فعال شود |
| تست کامل سبز | انجام‌نشده | ۵۴ تست اجرا شد؛ ۳۶ موفق | ۲ failure و ۱۶ error در تست‌های entity انبار |
| ساخت WAR | انجام‌شده | package با `-DskipTests` موفق | موفقیت build به معنی موفقیت تست یا deploy نیست |

## نتیجهٔ اجرای این نوبت — ۲۰۲۶/۱۰/۱۳

| دستور / مجموعه | نتیجه | توضیح |
|---|---|---|
| `.\mvnw.cmd -B -ntp test` | ناموفق | ۵۴ تست، ۲ failure، ۱۶ error، صفر skipped |
| Attachment | موفق | ۳۰ تست در entity، repository، mapper، service، REST و storage |
| Core | موفق | ۲ تست logging و ۴ تست BCrypt |
| Inventory | ناموفق | ۱۸ تست: ۲ failure و ۱۶ error |
| `.\mvnw.cmd -B -ntp -DskipTests package` | موفق | کامپایل کد و تست‌ها و تولید `target/MftOnlineStoreEE.war`؛ اجرای تست در این دستور رد شد |
| هشدار کامپایل | باقی است | مقدار اولیهٔ `Delivery.status` در `@SuperBuilder` نادیده گرفته می‌شود |
| Deploy، Redis، OpenAPI و مرورگر | انجام‌نشده در این نوبت | Maven این رفتارهای یکپارچه را تأیید نمی‌کند |

علت خطاهای فعلی تست انبار: در `src/test/resources/META-INF/persistence.xml`،
`testPU` فقط `Attachment` را ثبت کرده و `exclude-unlisted-classes=true` است.
Hibernate، `Inventory` را نمی‌شناسد و خطای `Unable to locate persister` می‌دهد.
برای ادامه، entityهای لازم و وابستگی‌های آن‌ها باید در واحد تست ثبت شوند؛
در این نوبت مطابق درخواست فقط READMEها تغییر کرده‌اند.

گزارش تفصیلی تست‌ها در `target/surefire-reports` است.

## اجرا

نیازمندی‌ها: JDK 17، Jakarta EE 10 و TomEE 10.2.0؛ یا Docker و Docker Compose.

<div dir="ltr">

```powershell
Copy-Item .env.example .env
docker compose up --build -d
```

</div>

کپی `.env` فقط در راه‌اندازی اولیه لازم است؛ فایل موجود را بازنویسی نکنید.
رمز `STORE_DB_PASSWORD` را در `.env` تغییر دهید.

- برنامه: [http://localhost:8080/](http://localhost:8080/)
- UI: [http://localhost:8080/index.html](http://localhost:8080/index.html)
- REST: `/api/v1/`
- Swagger UI: `/docs/index.html`
- سند OpenAPI: `/docs/attachments-openapi.json`

در استقرار غیر ROOT، context path برنامه را پیش از مسیرها اضافه کنید.
Bootstrap و Swagger UI از CDN بارگذاری می‌شوند و اینترنت لازم دارند.

| تنظیم | کاربرد / مقدار پیش‌فرض |
|---|---|
| `STORE_DB_NAME` | نام database در Compose؛ `store_db` |
| `STORE_DB_URL` | اتصال اجرای محلی؛ Compose مقدار `jdbc:postgresql://db:5432/...` را می‌سازد |
| `STORE_DB_USER` / `STORE_DB_PASSWORD` | اطلاعات اتصال؛ مقادیر نمونه فقط آموزشی‌اند |
| `POSTGRES_PORT` / `REDIS_PORT` / `APP_PORT` | پورت میزبان در Compose: ۵۴۳۳ / ۶۳۷۹ / ۸۰۸۰؛ فقط روی localhost |
| `STORE_REDIS_HOST` / `STORE_REDIS_PORT` | اجرای محلی: `localhost:6379`؛ داخل Compose: `redis:6379` |
| `STORE_UPLOAD_DIR` | مسیر فایل‌ها؛ داخل Compose: `/data/uploads` |

اجرای محلی بدون Docker: database را بسازید، تنظیمات اتصال را تعیین کنید،
سپس `.\mvnw.cmd -DskipTests package tomee:run` را اجرا کنید.
این دستور اجرای تست را رد می‌کند؛ برای بررسی تست‌ها دستور مستقل `test` را بزنید.

**هشدار داده:** تنظیم فعلی Hibernate برابر `create-drop` است؛ جدول‌ها و داده‌ها
با استقرار/خاموش‌شدن برنامه از بین می‌روند، حتی اگر volume PostgreSQL باقی باشد.
فایل‌های آپلود volume جدا دارند؛ Redis موقت است و volume ندارد.
`docker compose down` volumeها را نگه می‌دارد؛ `docker compose down -v` دادهٔ volumeها را حذف می‌کند.

## مستندات بخش‌ها

- [Core](src/main/java/com/mftplus/mftonlinestoreee/core/readme.md) و [امنیت](src/main/java/com/mftplus/mftonlinestoreee/core/security/readme.md)
- [Attachment و الگوی ماژول](src/main/java/com/mftplus/mftonlinestoreee/attachments/readme.md)
- [کاربران](src/main/java/com/mftplus/mftonlinestoreee/users/readme.md) و [محصولات](src/main/java/com/mftplus/mftonlinestoreee/products/readme.md)
- [سازمان](src/main/java/com/mftplus/mftonlinestoreee/organization/readme.md) و [کش](src/main/java/com/mftplus/mftonlinestoreee/organization/cache/readme.md)
- [سفارش](src/main/java/com/mftplus/mftonlinestoreee/orders/readme.md) و [انبار](src/main/java/com/mftplus/mftonlinestoreee/inventories/readme.md)
- [تیکت](src/main/java/com/mftplus/mftonlinestoreee/tickets/readme.md)، [ارسال](src/main/java/com/mftplus/mftonlinestoreee/delivery/readme.md) و [مالی](src/main/java/com/mftplus/mftonlinestoreee/financials/readme.md)
- [UI](src/main/webapp/assets/README.md) و [OpenAPI](src/main/webapp/docs/readme.md)
- [راهنمای دانشجو](src/main/java/com/mftplus/mftonlinestoreee/student-guide.md)

</div>
