<div dir="rtl">

# راهنمای دانشجو: ساختار ماژول‌ها و استفاده از core

این راهنما برای **همه‌ی ماژول‌ها** یکسان است. دستور کار مخصوص هر ماژول در فایل ‎`readme.md`‎ همان ماژول آمده است.

- پیش از شروع دستور کار، این راهنما را کامل بخوانید.
- در جدول‌ها به‌جای ‎`Xxx`‎ نام entity خودتان را بگذارید؛ مثلاً در ماژول products می‌شود ‎`Product`‎ و ‎`ProductRepository`‎.
- ماژول ‎`attachments`‎ نمونه‌ی کامل و درست همین ساختار است؛ توضیح قدم‌به‌قدم آن در ‎`attachments/readme.md`‎ آمده است.
- توضیح کامل کلاس‌های core در ‎`core/readme.md`‎ (بخش ۴) آمده است.

---

## ۱. ماژول از چه فایل‌هایی ساخته می‌شود

در پایان کار، پوشه‌ی هر ماژول باید این شکل را داشته باشد:

<div dir="ltr">

```
xxx/
├── entity/        Xxx
├── repository/    XxxRepository
├── dto/           XxxRequest, XxxDto
├── mapper/        XxxMapper
├── exception/     one class per business rule
├── service/       XxxService
│   └── impl/      XxxServiceImpl
└── rest/          XxxApi
```

</div>

اگر ماژول چند entity دارد، برای هر کدام همین مجموعه تکرار می‌شود.

## ۲. مسیر کامل یک request

مثال: ساختن یک product با ‎`POST /api/v1/products`‎.

<div dir="ltr">

```
client
  │   POST /api/v1/products    {"name":"Laptop","price":1200,"categoryId":1}
  │   Authorization: Basic ...   (optional: username and password)
  ▼
TomEE login (core/security/SecurityConfig)
  │   username + password checked against the database (BCrypt); no header -> anonymous
  ▼
JAX-RS (CXF in TomEE)
  │   RestApplication: every path starts with /api/v1
  │   finds ProductApi by @Path("/products") and @POST
  │   RolesAllowedFilter: checks @RolesAllowed / @PermitAll of ProductApi
  │   JSON body  ->  ProductRequest
  │   @Valid checks ProductRequest
  ▼
ProductApi                 no logic: calls the service
  ▼
ProductServiceImpl         @Transactional: a transaction starts
  │   business rules (name free? category exists?)
  │   ProductMapper: ProductRequest -> Product
  ▼
ProductRepository          save(...)  ->  INSERT
  ▼
database
  ▲
  │   (back up the same way)
ProductServiceImpl         ProductMapper: Product -> ProductDto, transaction commits
ProductApi                 Response 201 + ProductDto
ApiResponseFilter          wraps it: {"success":true,"status":201,"data":{...},"timestamp":"..."}
  ▼
client


If something goes wrong on the way, an exception is thrown and nobody catches it:

  no login, protected endpoint    ->  RolesAllowedFilter                  ->  401
  logged in, role missing         ->  RolesAllowedFilter                  ->  403
  @Valid fails                    ->  ConstraintViolationExceptionMapper  ->  400 + "errors"
  malformed JSON                  ->  GlobalExceptionMapper               ->  400
  NotFoundException (missing id)  ->  GlobalExceptionMapper               ->  404
  BusinessException (a rule)      ->  GlobalExceptionMapper               ->  409
  wrong HTTP method / media type  ->  GlobalExceptionMapper               ->  405 / 415
  any other exception             ->  GlobalExceptionMapper               ->  500 (stack trace only in the log)
  URL that matches no API         ->  ErrorPageServlet (web.xml)          ->  404

All of them answer: {"success":false,"status":...,"message":"...","timestamp":"..."}
```

</div>

به ترتیب:

1. **login** را خود TomEE انجام می‌دهد: اگر request هدر ‎`Authorization`‎ داشته باشد، username و password با database بررسی می‌شوند. بدون این هدر، request ناشناس است.
2. **JAX-RS** (در TomEE پیاده‌سازی‌اش CXF است) آدرس را می‌خواند. کلاس ‎`RestApplication`‎ باعث می‌شود همه‌ی مسیرها با ‎`/api/v1`‎ شروع شوند. سپس کلاس API درست را با ‎`@Path`‎ و متد HTTP پیدا می‌کند.
3. اگر کلاس API یا متد آن ‎`@RolesAllowed`‎ داشته باشد، کلاس ‎`RolesAllowedFilter`‎ در core بررسی می‌کند: بدون login جواب 401 است و بدون role لازم جواب 403. جزئیات در ‎`core/security/readme.md`‎ آمده است.
4. بدنه‌ی JSON به کلاس request تبدیل می‌شود و ‎`@Valid`‎ قواعد validation آن را بررسی می‌کند. اگر درست نبود، متد API اصلاً اجرا نمی‌شود.
5. **لایه‌ی API** فقط service را صدا می‌زند و DTO برمی‌گرداند؛ هیچ منطقی ندارد.
6. **لایه‌ی service** با ‎`@Transactional`‎ یک transaction باز می‌کند، قواعد business را بررسی می‌کند و با کمک mapper بین entity و DTO تبدیل می‌کند.
7. **لایه‌ی repository** تنها لایه‌ای است که با database کار می‌کند.
8. در راه برگشت، کلاس ‎`ApiResponseFilter`‎ در core جواب را در قالب یکسان ‎`ApiResponse`‎ می‌پیچد.
9. اگر در هر کدام از این قدم‌ها exception رخ دهد، کلاس‌های mapper خطا در core آن را می‌گیرند و به همان قالب JSON تبدیل می‌کنند.
10. آدرسی که به هیچ API‌ای نمی‌رسد، هرگز به کد شما نمی‌رسد؛ کلاس ‎`ErrorPageServlet`‎ جواب آن را با همان قالب می‌دهد.

## ۳. هر لایه از چه چیزی در core ارث می‌برد

کار تکراری را core انجام داده است؛ شما فقط چیزی را می‌نویسید که مخصوص ماژول خودتان است.

| لایه | کلاس شما | از core | چه چیزی آماده می‌گیرد |
|---|---|---|---|
| entity | ‎`Xxx`‎ | ‎`extends BaseEntity`‎ | فیلدهای ‎`id`‎، ‎`version`‎، ‎`deleted`‎، ‎`createdAt`‎، ‎`updatedAt`‎ و ‎`deletedAt`‎ (دوباره تعریفشان نکنید) |
| repository | ‎`XxxRepository`‎ | ‎`extends BaseRepository<Xxx>`‎ | ‎`save`‎، ‎`update`‎، ‎`findById`‎، ‎`findAll`‎، ‎`findPage`‎، ‎`count`‎، ‎`delete`‎، ‎`restore`‎ و ‎`findOneBy`‎ |
| mapper | ‎`XxxMapper`‎ | ‎`extends BaseMapper<Xxx, XxxDto, XxxRequest>`‎ | ‎`toDto`‎، ‎`toDtoList`‎، ‎`toEntity`‎ و ‎`updateEntity`‎ (کدشان را MapStruct می‌سازد) |
| exception | مثلاً ‎`DuplicateXxxNameException`‎ | ‎`extends BusinessException`‎ | تبدیل خودکار به جواب 409 همراه پیام |
| service | ‎`XxxService`‎ | ‎`extends BaseService<XxxRequest, XxxDto>`‎ | شش متد استاندارد: ‎`create`‎، ‎`findById`‎، ‎`findAll`‎، ‎`findAll(page, size)`‎، ‎`update`‎ و ‎`deleteById`‎ |
| service | ‎`XxxServiceImpl`‎ | ‎`implements XxxService`‎ | — (شش متد را خودتان پیاده می‌کنید) |
| paging | — | ‎`PageResult<T>`‎ | یک صفحه از ردیف‌ها همراه ‎`totalElements`‎ و ‎`totalPages`‎؛ با ‎`map(...)`‎ به DTO تبدیل می‌شود |
| REST | ‎`XxxApi`‎ | — | شروع مسیر با ‎`/api/v1`‎، قالب ‎`ApiResponse`‎ و تبدیل خطاها به JSON؛ همه خودکار |

## ۴. قواعد اصلی هر لایه

| لایه | قاعده |
|---|---|
| entity | نام جدول هم در ‎`@Table`‎ و هم در ‎`@SQLDelete`‎ بیاید. حذف از نوع soft delete است: ردیف پاک نمی‌شود و فقط ‎`deleted = true`‎ می‌شود. به‌جای ‎`@Builder`‎ از ‎`@SuperBuilder`‎ استفاده کنید. |
| repository | کلاس ‎`@ApplicationScoped`‎ باشد و متد ‎`entityType()`‎ را پیاده کند. ‎`EntityManager`‎ خودکار inject می‌شود. |
| DTO | دو record لازم است. **request** برای ورودی است؛ validation دارد و ‎`id`‎ ندارد. **dto** برای خروجی است و ‎`id`‎ دارد. در هیچ‌کدام entity نگذارید؛ به‌جای آن فقط ‎`id`‎ را بفرستید. |
| mapper | بالای این interface باید ‎`@Mapper(config = MapStructConfig.class)`‎ بیاید. فقط برای relationها خط ‎`@Mapping`‎ لازم است. |
| service | کلاس ‎`Impl`‎ با ‎`@ApplicationScoped`‎. بیرون از service فقط DTO دیده شود. هر متدی که چیزی را تغییر می‌دهد ‎`@Transactional`‎ بگیرد. |
| REST | کلاس ‎`@RequestScoped`‎. مسیر بدون ‎`/api/v1`‎ نوشته شود. DTO را مستقیم برگردانید و برای ‎`id`‎ ناموجود ‎`NotFoundException`‎ بدهید. ‎`XxxService`‎ را inject کنید، نه ‎`XxxServiceImpl`‎. |

## ۵. بخش‌هایی از core که خودکار کار می‌کنند (به آن‌ها دست نزنید)

| کلاس | کار |
|---|---|
| ‎`RestApplication`‎ | مسیر همه‌ی APIها را با ‎`/api/v1`‎ شروع می‌کند. |
| ‎`ApiResponseFilter`‎ | هر جواب موفق را در ‎`ApiResponse`‎ می‌پیچد. |
| ‎`GlobalExceptionMapper`‎ | exceptionها را به JSON تبدیل می‌کند: ‎`BusinessException`‎ به 409، ‎`NotFoundException`‎ به 404 و خطای پیش‌بینی‌نشده به 500. |
| ‎`ConstraintViolationExceptionMapper`‎ | خطاهای validation را به 400 همراه فهرست ‎`errors`‎ تبدیل می‌کند. |
| ‎`ErrorPageServlet`‎ | برای آدرسی که هیچ API‌ای ندارد هم همان قالب JSON را برمی‌گرداند. |
| ‎`EntityManagerProducer`‎ | ‎`EntityManager`‎ را برای ‎`BaseRepository`‎ فراهم می‌کند. |
| ‎`SecurityConfig`‎ | login با HTTP BASIC را انجام می‌دهد: username و password را با database و BCrypt بررسی می‌کند. |
| ‎`RolesAllowedFilter`‎ | ‎`@RolesAllowed`‎ و ‎`@PermitAll`‎ را روی کلاس API اجرا می‌کند: بدون login جواب 401 و بدون role لازم جواب 403. |

نتیجه: در ماژول شما **هیچ ‎`try/catch`‎، هیچ ‎`ApiResponse`‎ و هیچ مدیریت دستی transaction** لازم نیست.

## ۶. چرا در API نه ‎`try/catch`‎ می‌نویسید و نه ‎`ApiResponse`‎

برای اینکه همه‌ی ماژول‌های پروژه جواب **یکسان** بدهند، این دو کار یک بار در core انجام شده است:

- **جواب موفق:** شما فقط DTO را برمی‌گردانید. کلاس ‎`ApiResponseFilter`‎ بعد از اجرای متد، آن را در قالب ‎`ApiResponse`‎ می‌پیچد.
- **خطا:** شما فقط exception را throw می‌کنید. کلاس‌های ‎`GlobalExceptionMapper`‎ و ‎`ConstraintViolationExceptionMapper`‎ آن را می‌گیرند و به همان قالب با ‎`status`‎ درست تبدیل می‌کنند.

در نتیجه کلاینت برای همه‌ی ماژول‌ها فقط یک قالب می‌بیند:

<div dir="ltr">

```json
{"data":{"id":1,"name":"Laptop","price":1200,"categoryId":1,"categoryTitle":"Computers"},
 "status":201,"success":true,"timestamp":"2026-09-28T10:15:30Z"}
```

```json
{"message":"Product name 'Laptop' is already used","status":409,"success":false,"timestamp":"2026-09-28T10:15:31Z"}
```

```json
{"errors":["name: must not be blank","price: must not be null"],"message":"Validation failed",
 "status":400,"success":false,"timestamp":"2026-09-28T10:15:32Z"}
```

</div>

اگر در API خطا را با ‎`try/catch`‎ بگیرید یا خودتان ‎`ApiResponse`‎ بسازید، این یکسانی از بین می‌رود.

## ۷. ترتیب کار و وابستگی ماژول‌ها

بعضی ماژول‌ها از entity ماژول دیگری استفاده می‌کنند. اگر entity یک ماژول عوض شود، ماژول‌های وابسته هم باید با آن هماهنگ شوند.
جدول زیر وابستگی‌ها را **بعد از** مرحله‌ی ۱ همه‌ی ماژول‌ها نشان می‌دهد:

| ماژول | از entity کدام ماژول‌ها استفاده می‌کند |
|---|---|
| products | — |
| users | — (لیست ‎`branches`‎ از ‎`User`‎ حذف می‌شود) |
| financials | — (پرداخت فقط شماره‌ی سفارش را نگه می‌دارد) |
| organization | users |
| tickets | users |
| orders | products و users |
| inventories | products (فیلد ‎`order`‎ از ‎`InventoryTransaction`‎ حذف می‌شود) |

قواعد:

1. **مرحله‌ی ۱ (entity) ماژول‌های پایه زودتر از بقیه تحویل داده شود:** اول products و users؛ بعد organization و tickets و orders و inventories. ماژول financials مستقل است و از همان ابتدا شروع می‌شود.
2. دو وابستگی فعلی در مرحله‌ی ۱ حذف می‌شوند: وابستگی دوطرفه‌ی users و organization، و وابستگی inventories به orders.
3. entity ماژول گروه دیگری را خودتان تغییر ندهید؛ اگر تغییری لازم است، از همان گروه بخواهید.
4. قبل از هر تحویل، کل پروژه را build کنید؛ نه فقط ماژول خودتان.

## ۸. تحویل مرحله‌ای

دستور کار هر ماژول در دو تحویل انجام می‌شود:

| تحویل | مراحل | شامل | پیش از تحویل |
|---|---|---|---|
| تحویل اول: لایه‌ی داده | ۱ تا ۴ | entity و repository و DTO و mapper | کل پروژه کامپایل شود و deploy روی TomEE بدون خطا انجام شود (جدول‌ها در لاگ ساخته شوند). |
| تحویل دوم: منطق و API | ۵ تا ۹ | business ruleها و service و REST API و پاک‌سازی | همه‌ی موارد مرحله‌ی «چه وقت کار تمام است؟» در دستور کار ماژول انجام شده باشد. |

- بعد از تحویل اول، اشکال‌هایی را که گرفته می‌شود پیش از شروع مرحله‌ی ۵ برطرف کنید؛ مراحل بعد روی همین لایه ساخته می‌شوند.
- تست‌نویسی بعداً در یک دستور کار جدا اضافه می‌شود.

</div>
