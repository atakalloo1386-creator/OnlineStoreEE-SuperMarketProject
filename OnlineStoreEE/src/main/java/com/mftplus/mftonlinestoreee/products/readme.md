<div dir="rtl">

# ماژول محصولات و دسته‌بندی

مسئولیت: کاتالوگ کالا و دسته‌بندی‌های دارای parent اختیاری.
ماژول‌های سفارش و انبار از Product استفاده می‌کنند؛ تغییر قرارداد با آن‌ها هماهنگ شود.
الگو: [Attachment](../attachments/readme.md) و [راهنمای دانشجو](../student-guide.md).

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entity محصول و دسته‌بندی | انجام‌شده | BaseEntity، soft delete، قیمت BigDecimal و رابطه‌های Lazy |
| حذف لیست‌های معکوس غیرضروری | انجام‌شده | دسته‌بندی فقط parentCategory را نگه می‌دارد |
| repositoryهای جست‌وجو | انجام‌شده | نام، عنوان، category، زیر‌دسته و countهای موردنیاز |
| DTO بدون entity و mapping رابطه‌ها | انجام‌شده | categoryId/categoryTitle و parentId |
| mapping وضعیت فعال محصول | انجام‌نشده | request با `active` و entity با `isActive`؛ mapping صریح وجود ندارد |
| mapping وضعیت فعال در خروجی دسته‌بندی | انجام‌نشده | entity با `isActive` و DTO با `active`؛ خروجی تولیدشده مقدار null دارد |
| validation ورودی محصول | انجام‌شده | نام، قیمت مثبت و شناسهٔ دسته‌بندی |
| validation عنوان دسته‌بندی | ناقص | فقط Size دارد؛ عنوان null/خالی قبل از database رد نمی‌شود |
| CRUD و paging هر دو سرویس | انجام‌شده | repository و mapper استفاده می‌شوند؛ متدها stub نیستند |
| قواعد نام/عنوان تکراری، parent ناموجود و حذف دستهٔ در استفاده | انجام‌شده | helperها و BusinessExceptionها |
| جلوگیری از parent برابر خود دسته | انجام‌شده | `loadParent` |
| جلوگیری از چرخهٔ چندسطحی parent | انجام‌نشده | فقط خودارجاعی مستقیم کنترل شده است |
| خواندن و mapping Lazy در transaction | ناقص | خواندن‌های service annotation Transactional ندارند؛ روی TomEE باید بررسی/تکمیل شوند |
| REST فعلی | انجام‌شده | CRUD، page، فیلتر categoryId و sub-categories |
| UI محصول و دسته‌بندی | انجام‌شده | فرم‌ها آماده‌اند؛ mapping فعال در backend هنوز ناقص است |
| تست واقعی ماژول | انجام‌نشده | فایل‌های تست entity فقط setup/teardown دارند و Test ندارند |
| پاک‌سازی و بررسی HTTP | ناقص | کد کامنت‌شده و servletهای قدیمی باقی‌اند؛ اجرای سرور در این نوبت انجام نشد |

«انجام‌شده» یعنی پیاده‌سازی موجود است، نه تأیید نتیجهٔ عملی روی TomEE.

## قرارداد و مسیرهای فعلی

| بخش | ورودی / مسیر |
|---|---|
| ProductRequest | `name`، `price`، `description`، `active`، `categoryId` |
| ProductDto | خروجی وضعیت با نام `isActive` است |
| ProductCategoryRequest | `title`، `description`، `isActive`، `parentId` |
| ProductCategoryDto | خروجی وضعیت با نام `active` است |
| محصول | `/products`، `/products/{id}` و `/products/page` |
| فیلتر محصول | GET `/products?categoryId=5` |
| دسته‌بندی | `/products/categories`، `/{id}` و `/page` |
| زیر‌دسته‌ها | GET `/products/categories/{id}/sub-categories` |

prefix همهٔ APIها `/api/v1` است.
مسیر فعلی دسته‌بندی `/products/categories` است، نه مسیر پیشنهادی قدیمی
`/product-categories`.

## ترتیب ادامه

۱. نام یا mapping active/isActive را در هر دو جهت درست کنید؛ کد تولیدشدهٔ
MapStruct را هم بررسی کنید. policy IGNORE ممکن است خطا را در build نشان ندهد.
۲. عنوان دسته‌بندی را اجباری و غیرخالی کنید و چرخهٔ parent را بررسی کنید.
۳. خواندن DTO وابسته به relation را در transaction نگه دارید.
۴. تست mapper، repository، service و API اضافه و عملیات UI بررسی شوند.

## نتیجهٔ بررسی

کامپایل و ساخت WAR با رد اجرای تست در ۲۰۲۶/۱۰/۱۳ موفق شد؛
هیچ تست اجرایی محصول در گزارش Surefire وجود ندارد.
mappingهای ناقص با مطالعهٔ کد تولیدشده مشخص شدند، نه آزمون HTTP.
در این نوبت فقط این مستند تغییر کرده است.

</div>
