<div dir="rtl">

# زیرساخت Core

این بخش متعلق به مدرس است و شامل قراردادهای مشترک ماژول‌ها، تنظیمات JPA/JTA
و اتصال TomEE است. دانشجو برای ماژول خود از این کلاس‌ها استفاده می‌کند،
نه اینکه زیرساخت مشابه دیگری بسازد.
الگوی عملی: [Attachment](../attachments/readme.md).

## ۱. جدول تسک‌ها

«انجام‌شده» در این جدول یعنی پیاده‌سازی موجود است؛ تأیید رفتار روی سرور جداست.

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entity پایه، زمان‌ها و optimistic locking | انجام‌شده | `BaseEntity`، lifecycle و `@Version` |
| CRUD، paging، count و restore پایه | انجام‌شده | `BaseRepository` و `PageResult` |
| قرارداد DTO و mapper مشترک | انجام‌شده | `BaseService`، `BaseMapper` و `MapStructConfig` |
| تولید EntityManager و اتصال JTA | انجام‌شده | `EntityManagerProducer` و `TomEEJtaPlatform` |
| pool دیتابیس | انجام‌شده | `HikariDataSourceCreator` و دو datasource |
| پاسخ و خطای مشترک REST | انجام‌شده | `ApiResponseFilter`، exception mapperها و `ErrorPageServlet` |
| حفظ stream دانلود بدون پوشش JSON | انجام‌شده | `InputStream` در فیلتر پاسخ مستثنا شده است |
| logging بدون ثبت پارامترهای حساس | انجام‌شده | `Loggable` و `LoggingInterceptor` |
| hash رمز | انجام‌شده | `BCryptPasswordHash` |
| authentication و اجرای مجوزها | انجام‌نشده | `SecurityConfig` و `RolesAllowedFilter` در کد فعلی وجود ندارند |
| اجرای SQL امنیت | انجام‌نشده | propertyهای اجرای `security.sql` در persistence کامنت‌اند |
| تست logging و BCrypt | انجام‌شده | ۶ تست موفق در اجرای ۲۰۲۶/۱۰/۱۳ |
| پوشش خودکار سایر اجزای Core | انجام‌نشده | تست مستقیم فیلتر پاسخ، mapperهای خطا و integration لازم است |
| تنظیم واحد تست برای انبار | ناقص | `testPU` فقط Attachment را ثبت کرده؛ ۱۸ تست انبار ناموفق‌اند |
| تبدیل خطاهای قفل/constraint به 409 | انجام‌نشده | در نگاشت عمومی فعلی به 500 می‌رسند |
| بررسی یکپارچهٔ نسخهٔ فعلی روی TomEE | انجام‌نشده | در این نوبت فقط Maven test/package اجرا شد |

## ۲. فناوری‌ها و تقسیم مسئولیت

JDK 17 و TomEE 10.2.0 با Jakarta EE 10 استفاده می‌شوند.
TomEE خدمات CDI، REST، JSON-B، Validation و JTA را فراهم می‌کند.
WAR شامل Hibernate، HikariCP، PostgreSQL JDBC، MapStruct، Logback و Jedis است.
Lombok و پردازشگر MapStruct هنگام کامپایل کد تولید می‌کنند؛
JUnit، Mockito و H2 فقط برای تست‌اند.

## ۳. مسیر درخواست

`REST → Service → Repository → EntityManager → PostgreSQL`

mapper در service، entity را به DTO تبدیل می‌کند. پاسخ JSON پس از خروج از API
توسط Core پوشش داده می‌شود؛ خطاها به exception mapper مشترک می‌رسند.

## ۴. قراردادهای پایه

| کلاس | روش استفاده |
|---|---|
| `BaseEntity` | entity از آن ارث می‌برد؛ id، version، deleted و زمان‌ها را دوباره تعریف نکنید |
| `BaseRepository<Entity>` | `entityType()` را مشخص کنید؛ فقط finderهای خاص ماژول را اضافه کنید |
| `BaseService<Request, Dto>` | CRUD و `findAll(page, size)`؛ خروجی DTO و برای رکورد ناموجود Optional خالی |
| `BaseMapper<Entity, Dto, Request>` | `toDto`، `toDtoList`، `toEntity` و `updateEntity` |
| `MapStructConfig` | mapper تولیدشده CDI است؛ فیلدهای نامنطبق نادیده گرفته می‌شوند |
| `PageResult` | محتوای صفحه و تعدادها؛ `map(mapper::toDto)` تعدادها را نگه می‌دارد |
| `BusinessException` | پایهٔ خطاهای قواعد کسب‌وکار؛ پاسخ 409 |
| `RestApplication` | prefix مشترک `/api/v1`؛ در API ماژول تکرار نکنید |

نکتهٔ paging: `repository.findAll(page, size)` فقط List می‌دهد؛
برای صفحه همراه تعدادها از `repository.findPage(page, size)` استفاده کنید.

Soft delete در entity فرزند با `@SQLDelete` و `@SQLRestriction("deleted = false")`
تعریف می‌شود. نام جدول در SQL باید با `@Table` یکی باشد.
`restore(id)` یک UPDATE native است و transaction لازم دارد.

فیلدهای هم‌نام خودکار map می‌شوند؛ رابطه‌ها و نام‌های متفاوت نیاز به
`@Mapping` دارند. چون policy برابر IGNORE است، کامپایل موفق نبودن mapping را
اثبات نمی‌کند؛ کد تولیدشده در `target/generated-sources/annotations` قابل مطالعه است.

## ۵. پاسخ و logging

`ApiResponse` دارای `success`، `status`، `message`، `data`، `errors`
و `timestamp` است. API ماژول DTO برمی‌گرداند، نه پوشش پاسخ مشترک.
پاسخ 204، stream، پاسخ غیر JSON، خطا و ApiResponse آماده دوباره پوشش داده نمی‌شوند.

`@Loggable` روی کلاس service implementation قرار می‌گیرد.
مدت اجرا و نتیجه ثبت می‌شود، نه رمز و محتوای request.
فراخوانی داخلی همان شیء از proxy/interceptor عبور نمی‌کند.

## ۶. فایل‌های تنظیمات

| فایل | مسئولیت / وضعیت فعلی |
|---|---|
| `config/EntityManagerProducer.java` | تولید CDI EntityManager مربوط به `storePU` |
| `integration/tomee/HikariDataSourceCreator.java` | ساخت pool Hikari برای TomEE |
| `integration/tomee/TomEEJtaPlatform.java` | اتصال Hibernate به transaction manager سرور |
| `META-INF/persistence.xml` | Hibernate، JTA و `create-drop`؛ seed امنیت غیرفعال است |
| `WEB-INF/resources.xml` | `PostgresDS` با JTA و `PostgresNonJtaDS` بدون JTA |
| `WEB-INF/beans.xml` | کشف beanهای دارای annotation |
| `WEB-INF/web.xml` | error page؛ تنظیم `jdbc/SecurityDS` فعلاً ندارد |
| `logback.xml` | logging برنامه، Hibernate و pool |
| `src/test/resources/META-INF/persistence.xml` | H2، RESOURCE_LOCAL، فقط entity ثبت‌شدهٔ Attachment |
| `pom.xml` | Java 17، پردازشگرها، Surefire، WAR و TomEE Maven plugin |

اتصال از `STORE_DB_URL`، `STORE_DB_USER` و `STORE_DB_PASSWORD` خوانده می‌شود.
پیش‌فرض محلی resources: `jdbc:postgresql://localhost:5432/store_db`،
کاربر `postgres`؛ رمز پیش‌فرض آموزشی است و باید با تنظیم محیط جایگزین شود.

**create-drop داده را نگه نمی‌دارد**؛ volume Docker این رفتار Hibernate را عوض نمی‌کند.
در این پروژه migration و تنظیم production اضافه نشده است.

## ۷. Transaction

- روی عملیات نوشتن service، `jakarta.transaction.Transactional` بگذارید.
- entity خوانده‌شده داخل همان transaction، managed است؛ تغییر آن با dirty checking
  ذخیره می‌شود و معمولاً merge مجدد لازم ندارد.
- اگر خواندن DTO به رابطهٔ Lazy دست می‌زند، خواندن و mapping را در transaction انجام دهید.
- فراخوانی یک متد annotated از داخل همان کلاس transaction جدید باز نمی‌کند؛
  annotation باید روی ورودی‌ای باشد که bean دیگر صدا می‌زند.
- تست‌های H2 بدون TomEE از `em.getTransaction()` استفاده می‌کنند.

## ۸. Validation

قواعد ورودی روی Request DTO هستند. روی بدنهٔ API از `@NotNull @Valid`
و روی paging از `@Min(0)`، `@Min(1)` و `@Max(100)` استفاده کنید.
annotation مناسب نوع را انتخاب کنید: `@NotBlank` برای String، نه Boolean یا عدد.
annotationهای ورودی generic interface جای validation صریح API را نمی‌گیرند.

## ۹. خطاها

| خطا | پاسخ فعلی |
|---|---|
| validation ورودی | 400 با جزئیات فیلدها |
| JSON نامعتبر | 400 |
| `NotFoundException` | 404 |
| `BusinessException` | 409 |
| `WebApplicationException` | status خود exception |
| خطای پیش‌بینی‌نشده | 500؛ جزئیات داخلی فقط در log |

برای خطای کسب‌وکار، exception ماژول را throw کنید؛ در API try/catch یا
exception mapper جدید نسازید. اجرای واقعی 401/403 وابسته به تکمیل امنیت است،
نه صرفاً وجود annotation.

## ۱۰. اجرا و build

[README ریشه](../../../../../../../readme.md) شامل Docker و تنظیمات اجرا است.
در ریشهٔ پروژه:

<div dir="ltr">

```powershell
.\mvnw.cmd -B -ntp test
.\mvnw.cmd -B -ntp -DskipTests package
```

</div>

اجرای ۲۰۲۶/۱۰/۱۳: ۵۴ تست، ۳۶ موفق، ۲ failure و ۱۶ error در انبار.
ساخت جداگانهٔ WAR با رد اجرای تست موفق شد.
هشدار `Delivery.status` باقی است؛ در این نوبت هیچ کد یا تنظیمی اصلاح نشده است.

## ۱۱. ادامهٔ کار

اول تنظیم testPU و کارهای امنیت را تکمیل کنید؛ سپس روی TomEE، validation،
پوشش پاسخ، فایل JSON، مجوزها و transaction واقعی را بررسی کنید.
موفقیت تست‌های مستقیم Mockito به معنی تأیید CDI یا HTTP نیست.

</div>
