<div dir="rtl">

# انبار، موجودی و گردش کالا

Inventory انبار است؛ InventoryItem موجودی یک Product در آن انبار است.
InventoryTransaction ورود/خروج/اصلاح موجودی را ثبت می‌کند.
وابسته به Product است و موجودی فقط از مسیر ثبت گردش تغییر می‌کند.
الگو: [Attachment](../attachments/readme.md).

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entityهای انبار، موجودی و گردش | انجام‌شده | BaseEntity و soft delete؛ گردش به InventoryItem متصل است |
| رابطهٔ انبار/محصول و تاریخ انقضا | انجام‌شده | ManyToOne Lazy؛ expirationDate اختیاری از نوع LocalDate |
| یکتایی inventory/product | انجام‌شده | بررسی service و constraint ترکیبی database |
| repositoryهای finder و count | انجام‌شده | نام انبار، موجودی انبار، محصول، low-stock و گردش item |
| DTO و mapper رابطه‌ها | انجام‌شده | شناسه‌ها و نام محصول؛ Request موجودی quantity نمی‌گیرد |
| CRUD و paging انبار/موجودی | انجام‌شده | serviceهای واقعی و دو API |
| قواعد نام تکراری و entity ناموجود | انجام‌شده | helperهای service و BusinessException |
| جلوگیری از حذف انبار غیرخالی / موجودی غیرصفر | انجام‌شده | count ردیف و quantity بررسی می‌شوند |
| موجودی اولیهٔ صفر | انجام‌شده | InventoryItem.create |
| ثبت گردش و تغییر موجودی در یک transaction | انجام‌شده | `register` با Transactional |
| IN / OUT / RETURN / ADJUSTMENT و جلوگیری از موجودی منفی | انجام‌شده | `applyQuantity` |
| گردش append-only در API | انجام‌شده | فقط GET/POST زیر inventory-items |
| فهرست موجودی‌های انبار در REST | انجام‌نشده | سرویس findByInventory هست، اما مسیر `/inventories/{id}/items` وجود ندارد |
| mapping Lazy در خواندن‌های service | ناقص | خواندن InventoryItem بدون Transactional؛ روی TomEE بررسی/تکمیل شود |
| سازگاری یکتایی database با soft delete | ناقص | رکورد حذف‌شده هنوز ترکیب یکتا را نگه می‌دارد؛ سناریوی ایجاد مجدد بررسی شود |
| UI انبار، موجودی و گردش | انجام‌شده | CRUD، low-stock و فرم گردش؛ ویرایش/حذف گردش غیرفعال است |
| نوشتن تست entity | انجام‌شده | ۱۸ تست نوشته شده‌اند، اما اجرای آن‌ها موفق نیست |
| تنظیم testPU برای entityهای انبار | انجام‌نشده | فقط Attachment ثبت شده؛ Hibernate انبار را نمی‌شناسد |
| تست mapper/repository/service/API | انجام‌نشده | فایل‌های این لایه‌ها خالی‌اند و تست اجرایی ندارند |
| بررسی API و موجودی نهایی روی TomEE | انجام‌نشده | در این نوبت deploy یا تست HTTP انجام نشد |

«انجام‌شده» یعنی کد مربوط موجود است؛ نتیجهٔ آزمون جدا در پایین آمده است.

## قواعد گردش

| نوع | quantity مجاز | اثر |
|---|---|---|
| IN | مثبت | اضافه‌کردن |
| RETURN | مثبت | اضافه‌کردن |
| OUT | مثبت | کم‌کردن |
| ADJUSTMENT | غیرصفر؛ مثبت یا منفی | اعمال با علامت خودش |

پس از اعمال، موجودی منفی رد می‌شود.
Request شامل type، quantity، referenceType، referenceId اختیاری و reason است.
refererenceTypeهای فعلی ORDER و MANUAL هستند.
ثبت گردش و تغییر موجودی باید با هم commit/rollback شوند؛ `@Version`
برای تداخل تغییرهای هم‌زمان موجود است، اما این سناریو تست نشده است.

## مسیرهای فعلی

همه پس از `/api/v1` هستند.

| مسیر | عملیات |
|---|---|
| `/inventories` و `/inventories/{id}` | CRUD |
| `/inventories/page` | GET صفحه |
| `/inventory-items` و `/inventory-items/{id}` | CRUD |
| `/inventory-items/page` | GET صفحه |
| `/inventory-items/low-stock` | GET کمبود موجودی |
| `/inventory-items/{itemId}/transactions` | GET گردش‌ها / POST ثبت گردش |

## نتیجهٔ تست — ۲۰۲۶/۱۰/۱۳

| مجموعه | تعداد | failure | error | نتیجه |
|---|---|---|---|---|
| InventoryTest | ۵ | ۲ | ۳ | ناموفق |
| InventoryItemTest | ۶ | ۰ | ۶ | ناموفق |
| InventoryTransactionTest | ۷ | ۰ | ۷ | ناموفق |
| مجموع انبار | ۱۸ | ۲ | ۱۶ | ناموفق |

در `src/test/resources/META-INF/persistence.xml`، واحد `testPU`
با exclude-unlisted-classes فقط Attachment را ثبت می‌کند.
خطا `Unable to locate persister: ...Inventory` است؛ دو failure نیز
انتظار PersistenceException دارند، اما به همین علت IllegalArgumentException می‌گیرند.
پس این اجرا قواعد موجودی و mapping انبار را تأیید نکرده است.

## ترتیب ادامه

۱. entityهای Inventory، InventoryItem، InventoryTransaction و وابستگی‌های لازم
را در testPU ثبت و تست‌ها را دوباره اجرا کنید.
۲. تست‌های خالی لایه‌های دیگر را تکمیل کنید.
۳. خواندن Lazy، فهرست موجودی انبار و ایجاد مجدد پس از soft delete بررسی شوند.
۴. با گردش‌های متوالی، موجودی و تاریخچه را روی TomEE و UI تطبیق دهید.

ساخت WAR با `-DskipTests` موفق است؛ خطاهای تست فقط مستند شده‌اند و
هیچ کد یا تنظیمی در این نوبت تغییر نکرده است.

</div>
