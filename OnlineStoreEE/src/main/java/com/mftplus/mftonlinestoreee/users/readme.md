<div dir="rtl">

# ماژول کاربران، نقش و پروفایل

مسئولیت: اطلاعات کاربر و تخصیص نقش‌ها. احراز هویت سرور مربوط به
[Core Security](../core/security/readme.md) است، نه CRUD کاربران.
الگوی تکمیل: [Attachment](../attachments/readme.md) و [راهنمای دانشجو](../student-guide.md).

## جدول تسک‌ها

«انجام‌شده» یعنی کد مربوط موجود است؛ این ماژول تست خودکار اجرایی ندارد.

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| User، Role، Profile و جدول واسط نقش‌ها | انجام‌شده | BaseEntity، soft delete، `user_roles` و Profile.user یکتا |
| حذف وابستگی معکوس User به شعبه | انجام‌شده | لیست branches در User وجود ندارد |
| repositoryهای کاربر، نقش و پروفایل | انجام‌شده | `findByUsername`، `countByRole`، `findByName` و `findByUser` |
| DTO و mapper | انجام‌شده | شناسه‌ها در ورودی؛ نام نقش‌ها در خروجی کاربر؛ رمز در UserDto نیست |
| CRUD و paging کاربر | انجام‌شده | `UserServiceImpl` و `UserApi` |
| username تکراری و role ناموجود | انجام‌شده | بررسی در service و exception کسب‌وکار |
| hash رمز و تخصیص roleIds | انجام‌شده | create/update از BCrypt و RoleRepository استفاده می‌کنند |
| ساخت و خواندن نقش | ناقص | save/list/id/page در service هست؛ `checkNameIsFree` خالی است |
| جلوگیری از نام نقش تکراری | انجام‌نشده | `RoleServiceImpl.checkNameIsFree` پیاده نشده |
| ویرایش نقش | انجام‌نشده | `RoleServiceImpl.update` همیشه Optional خالی می‌دهد |
| حذف نقش و جلوگیری از حذف نقش در استفاده | انجام‌نشده | `deleteById` و `checkNotInUse` پیاده نشده‌اند |
| مسیرهای پروفایل | انجام‌شده | GET/PUT `/users/{id}/profile` از مسیر کاربر جداست |
| خواندن و ذخیرهٔ پروفایل | انجام‌نشده | `findProfile` و `saveProfile` همیشه Optional خالی‌اند |
| UI کاربران، نقش و پروفایل | انجام‌شده | فرم و مسیر آماده؛ نقش/پروفایل به تکمیل service وابسته‌اند |
| پاک‌سازی کد آموزشی قدیمی | ناقص | `loadRoles` خالی و servlet بلااستفاده باقی است |
| تست ماژول و بررسی endpointها روی سرور | انجام‌نشده | در اجرای فعلی تستی برای users اجرا نشد |

## قرارداد فعلی

- UserRequest: `username`، `password`، `enabled` و `roleIds`.
- UserDto: `id`، `username`، `enabled` و مجموعهٔ نام نقش‌ها.
- یک request برای create/update است؛ تغییر کاربر نیز رمز می‌خواهد و دوباره hash می‌کند.
- roleIds خالی یا null، مجموعهٔ نقش‌های کاربر را خالی می‌کند.
- roles در entity فعلاً EAGER هستند؛ پروفایل رابطهٔ یک‌به‌یک دارد.
- یکتایی username در service بررسی می‌شود؛ ادعای وجود constraint یکتای database نداریم.

## مسیرهای موجود

همه پس از `/api/v1` قرار می‌گیرند.

| مسیر | عملیات موجود در API | محدودیت |
|---|---|---|
| `/users` و `/users/{id}` | GET، POST، PUT، DELETE | CRUD پیاده شده؛ اجرای مجوز admin به تکمیل امنیت وابسته است |
| `/users/page` | GET با page/size | paging |
| `/roles` و `/roles/{id}` | GET، POST، PUT، DELETE | PUT/DELETE هنوز به service خالی می‌رسند |
| `/users/{id}/profile` | GET، PUT | فعلاً service خالی است و API پاسخ 404 می‌دهد |

## ترتیب ادامه

۱. قواعد نام نقش، update/delete و قاعدهٔ نقش در استفاده را تکمیل کنید.
۲. ProfileRepository و ProfileMapper را به سرویس پروفایل وصل کنید؛
user ناموجود Optional خالی و ذخیرهٔ پروفایل داخل transaction باشد.
۳. helper خالی و servlet قدیمی را پاک‌سازی و تست‌های ماژول را اضافه کنید.
۴. قرارداد profile/role را از UI و سپس روی TomEE بررسی کنید.

## نتیجهٔ بررسی

کامپایل ماژول همراه پروژه و package با `-DskipTests` در ۲۰۲۶/۱۰/۱۳ موفق شد.
۴ تست BCrypt متعلق به Core موفق‌اند؛ این نتیجه تست CRUD کاربر، نقش یا پروفایل نیست.
هیچ کد این بخش در این نوبت تغییر نکرده است.

</div>
