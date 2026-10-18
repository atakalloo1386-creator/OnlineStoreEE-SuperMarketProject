<div dir="rtl">

# Attachment — ماژول مرجع آموزشی

این ماژول نمونهٔ مدرس برای پیاده‌سازی لایه‌های Jakarta EE است.
مشخصات پیوست در database و محتوای فایل روی disk نگه‌داری می‌شود.
ماژول‌های دانشجویی ساختار آن را الگو بگیرند؛ بخش storage فقط برای فایل لازم است.

## ۱. ساختار و جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entity و soft delete | انجام‌شده | `Attachment`، `BaseEntity` و enum مالک |
| repository و جست‌وجوی مالک | انجام‌شده | `findByOwner` و `findByOwnerAndFileName` |
| DTO ورودی/خروجی و mapper | انجام‌شده | recordها، validation و MapStruct |
| CRUD و paging | انجام‌شده | `AttachmentServiceImpl` و `AttachmentApi` |
| نام فایل یکتا برای مالک | انجام‌شده | بررسی service بین رکوردهای حذف‌نشده؛ exception با پاسخ 409 |
| ذخیرهٔ stream با نام UUID | انجام‌شده | `FileStorage`؛ شمارش اندازه در سرور |
| آپلود و دانلود محتوا | انجام‌شده | `/{id}/content` و هدرهای نوع/نام فایل |
| حفظ دانلود JSON بدون پوشش پاسخ | انجام‌شده | استثنای InputStream در فیلتر Core |
| annotationهای دسترسی | انجام‌شده | metadata عمومی، نوشتن admin، محتوا با `attachment:file` |
| اجرای واقعی مجوزها | انجام‌نشده | زیرساخت authentication/authorization کامل نیست |
| OpenAPI و Swagger UI | انجام‌شده | `docs/attachments-openapi.json` و `docs/index.html` |
| صفحهٔ UI پیوست | انجام‌شده | metadata، فیلتر مالک، آپلود و دانلود |
| تست entity / mapper / repository / service / API / storage | انجام‌شده | ۳۰ تست موفق در اجرای ۲۰۲۶/۱۰/۱۳ |
| بررسی HTTP، JSON-B و مجوزها روی نسخهٔ فعلی | انجام‌نشده | در این نوبت فقط Maven اجرا شد؛ تست‌های مستقیم جای deploy نیستند |

## ۲. مسیر درخواست

`REST → Service → Repository → PostgreSQL`
و برای محتوا: `Service → FileStorage → Disk`.

ایجاد پیوست دو مرحله دارد: ابتدا POST مشخصات JSON، سپس PUT فایل برای همان id.
service از mapper استفاده می‌کند و DTO می‌دهد؛ Core پاسخ JSON را در `data`
قرار می‌دهد. خطاها توسط exception mapper مشترک پاسخ داده می‌شوند.

## ۳. ترتیب پیاده‌سازی یک ماژول

### مرحلهٔ ۱ — Entity

از `BaseEntity` ارث ببرید؛ شناسه، version و زمان‌ها را تکرار نکنید.
نام جدول در `@Table` و `@SQLDelete` یکی باشد و
`@SQLRestriction("deleted = false")` را اضافه کنید.
enum با `EnumType.STRING` ذخیره شود. ستون‌ها با طول و الزام Request سازگار باشند.

### مرحلهٔ ۲ — Repository

از `BaseRepository<Entity>` ارث ببرید و `entityType()` را مشخص کنید.
CRUD و paging را دوباره ننویسید. finderهای خاص با JPQL و پارامتر bindشده نوشته شوند.
repository دارای `@ApplicationScoped` است؛ constructor دارای EntityManager
برای تست بدون CDI قابل استفاده است.

### مرحلهٔ ۳ — DTO

Request و DTO خروجی، recordهای جدا باشند.
Request شناسهٔ entity مرتبط را می‌گیرد، نه خود entity را.
validation روی ورودی است؛ فیلدهای داخلی مانند `storedName` در خروجی نمی‌آیند.
در Attachment، `contentType` و `fileSize` را سرور پس از آپلود تعیین می‌کند.

### مرحلهٔ ۴ — Mapper

از `BaseMapper<Entity, Dto, Request>` با `MapStructConfig` استفاده کنید.
`id` و `version` در create/update از request کپی نمی‌شوند.
برای نام متفاوت و relation، `@Mapping` صریح لازم است؛
policy برابر IGNORE نبودن mapping را بی‌صدا باقی می‌گذارد.
entity مرتبط را service از repository بارگذاری می‌کند، نه mapper.

### مرحلهٔ ۵ — Exception

برای قاعدهٔ کسب‌وکار از `BusinessException` ارث ببرید.
`DuplicateFileNameException` نمونهٔ پاسخ 409 است.
رکورد ناموجود در service، Optional خالی و در REST، `NotFoundException` با 404 است.

### مرحلهٔ ۶ — Service

interface از `BaseService<Request, Dto>` ارث می‌برد؛ impl دارای
`@ApplicationScoped`، تزریق repository/mapper و `@Transactional` روی نوشتن است.
برای DTO وابسته به Lazy، خواندن و mapping هم در transaction باشند.
entity managed با تغییر فیلدها ذخیره می‌شود؛ merge اضافه لازم نیست.
paging با `repository.findPage(page, size).map(mapper::toDto)` انجام می‌شود.

### مرحلهٔ ۷ — REST

API دارای `@RequestScoped` و `@Path("/attachments")` است؛
prefix `/api/v1` از Core می‌آید.
interface سرویس inject شود و body با `@NotNull @Valid` بررسی شود.
پاسخ ایجاد 201، ویرایش 200 و حذف 204 است.
DTO را برگردانید؛ ApiResponse و try/catch در هر endpoint تکرار نشود.
annotationهای امنیت، قرارداد دسترسی را بیان می‌کنند؛ اجرای آن‌ها هنوز باید تکمیل شود.

## ۴. مالک پیوست

`ownerType` و `ownerId` مشخص می‌کنند فایل متعلق به چیست.
نوع‌های فعلی: `PRODUCT`، `USER`، `ORGANIZATION`، `BRANCH`،
`TICKET`، `TICKET_MESSAGE`، `PAYMENT` و `ORDER`.

Attachment به entityهای این ماژول‌ها وابسته نیست و وجود مالک را بررسی نمی‌کند.
نام فایل برای ترکیب نوع و شناسهٔ مالک بین پیوست‌های حذف‌نشده یکتا بررسی می‌شود؛
در دیتابیس constraint یکتای این ترکیب وجود ندارد.

## ۵. فایل روی disk

- مسیر از `STORE_UPLOAD_DIR` خوانده می‌شود؛ پیش‌فرض
  `<user.home>/mft-store/uploads` و در Compose، `/data/uploads` است.
- نام واقعی disk یک UUID است؛ نام اصلی فقط در metadata و هدر دانلود می‌ماند.
- body آپلود raw bytes است؛ JSON یا multipart نیست.
- `Content-Type` ورودی ذخیره می‌شود؛ محتوا برای تشخیص نوع واقعی بررسی نمی‌شود.
- `Files.copy` فایل را با stream ذخیره می‌کند و اندازهٔ واقعی را برمی‌گرداند.
- دانلود، InputStream و هدر `Content-Disposition` با نام UTF-8 دارد.
- آپلود مجدد فایل تازه می‌سازد؛ فایل قبلی باقی می‌ماند.
- حذف metadata، soft delete است و فایل را پاک نمی‌کند.
- rollback دیتابیس فایل ذخیره‌شده را خودکار پاک نمی‌کند.
- محدودیت اندازه، پاک‌سازی فایل یتیم و اسکن فایل فعلاً خارج از دامنهٔ مثال‌اند.

با `create-drop` مشخصات database از بین می‌روند، اما فایل‌های disk باقی می‌مانند.

## ۶. اجزای استفاده‌شده از Core

`BaseEntity`، `BaseRepository`، `BaseService`، `BaseMapper`،
`PageResult`، `BusinessException`، logging و فیلتر/mapperهای مشترک.
[Core](../core/readme.md) و [وضعیت امنیت](../core/security/readme.md) را ببینید.
`RolesAllowedFilter` در کد فعلی موجود نیست و نباید جزئی از زیرساخت آماده فرض شود.

## ۷. REST و OpenAPI

همهٔ مسیرهای جدول پس از `/api/v1` قرار می‌گیرند.

| HTTP | مسیر | ورودی / نتیجه |
|---|---|---|
| GET | `/attachments` | همه؛ یا فیلتر هم‌زمان `ownerType` و `ownerId` |
| GET | `/attachments/page?page=0&size=10` | صفحه؛ size بین ۱ تا ۱۰۰ |
| GET | `/attachments/{id}` | DTO؛ ناموجود 404 |
| POST | `/attachments` | AttachmentRequest؛ پاسخ 201 |
| PUT | `/attachments/{id}` | AttachmentRequest؛ پاسخ 200 |
| DELETE | `/attachments/{id}` | حذف نرم؛ پاسخ 204 |
| PUT | `/attachments/{id}/content` | فایل خام؛ پاسخ DTO |
| GET | `/attachments/{id}/content` | فایل خام؛ بدون فایل یا رکورد، 404 |

Request: `ownerType`، `ownerId`، `title`، `fileName`،
`description` و `keywords`.
metadata خروجی شامل id و اطلاعات فایل است، نه نام داخلی ذخیره‌سازی.

Swagger UI در `/docs/index.html` و سند ثابت در
`/docs/attachments-openapi.json` است؛ سند خودکار از Java تولید نمی‌شود.
قرارداد آن باید همراه AttachmentApi و DTOها به‌روز شود.
Security در سند مقصود دسترسی را بیان می‌کند، نه تأیید فعال‌بودن امنیت سرور.

## ۸. تست‌ها

| لایه | تعداد | نتیجهٔ ۲۰۲۶/۱۰/۱۳ |
|---|---|---|
| entity | ۴ | موفق |
| mapper | ۳ | موفق |
| repository | ۵ | موفق |
| REST | ۸ | موفق |
| service | ۸ | موفق |
| storage | ۲ | موفق |
| مجموع | ۳۰ | بدون failure یا error |

entity/repository با H2، storage با پوشهٔ موقت و service/API با Mockito بررسی می‌شوند.
تست REST مستقیم، annotation امنیت، validation سرور و wrapper HTTP را اجرا نمی‌کند.

در ریشه: `.\mvnw.cmd test -Dtest=AttachmentServiceImplTest`.
کل suite اکنون به‌دلیل تنظیم testPU انبار ناموفق است؛ این مسئله از تست‌های
Attachment نیست. package با `-DskipTests` موفق شد.

## ۹. چک‌لیست تحویل ماژول دانشجویی

۱. entity و relationها، repository، DTO و mapper مطابق قرارداد Core.
۲. service واقعی بدون متد stub؛ validation و قواعد کسب‌وکار.
۳. CRUD، paging، 404 و 409 در REST؛ عملیات وضعیت جدا از ویرایش عمومی.
۴. در DTO هیچ entity و اطلاعات حساس نباشد.
۵. تست‌ها نوشته شوند؛ entityهای مرتبط در testPU ثبت شوند.
۶. روی TomEE، JSON، مجوزها، validation و transaction یک بار بررسی شوند.
۷. فرم UI با نام فیلدها و مسیرهای واقعی API هماهنگ باشد.

## ۱۰. اشتباه‌های متداول

| اشتباه | اصلاح |
|---|---|
| `@NotBlank` روی عدد یا enum | annotation متناسب با نوع |
| entity داخل DTO | شناسه و فیلدهای ساده |
| کپی prefix `/api/v1` در API | فقط مسیر ماژول |
| نبودن transaction هنگام mapping Lazy | خواندن و mapping در transaction |
| نبودن bean scope مناسب | repository/service: ApplicationScoped؛ API: RequestScoped |
| فراخوانی داخلی متد Transactional | annotation روی ورودی bean یا bean جدا |
| نام متفاوت بدون mapping | `@Mapping` صریح و بررسی کد تولیدشده |
| نوشتن فایل با نام ارسالی | UUID و پوشهٔ مستقل از WAR |
| فرض امنیت از روی annotation | تنظیم و بررسی واقعی authentication/authorization |

</div>
