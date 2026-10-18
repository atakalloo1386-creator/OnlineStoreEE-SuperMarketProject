<div dir="rtl">

# تیکت و پیام‌ها

تیکت متعلق به یک User است؛ هر پیام یک sender و یک Ticket دارد.
پیام در API فقط اضافه و خوانده می‌شود، نه ویرایش/حذف.
الگو: [Attachment](../attachments/readme.md) و [راهنمای دانشجو](../student-guide.md).

## جدول تسک‌ها

| تسک | وضعیت | شاهد / کار باقی‌مانده |
|---|---|---|
| entityها و soft delete | انجام‌شده | رابطه‌های User/Ticket/Sender از نوع ManyToOne Lazy |
| حذف receiver و لیست معکوس پیام‌ها | انجام‌شده | رابطه‌های اضافی قدیمی وجود ندارند |
| repositoryهای findByUser / findByTicket | انجام‌شده | خروجی List پیام‌ها و تیکت‌ها |
| DTO بدون entity و mapper رابطه‌ها | انجام‌شده | شناسه‌ها و نام‌ها در خروجی |
| ساده‌سازی Request تیکت | انجام‌نشده | timestamp، username و status هنوز از client خواسته می‌شوند |
| ساده‌سازی Request پیام | ناقص | ticketId هم در body و هم در URL است؛ createdAt نیز در request وجود دارد |
| CRUD تیکت | انجام‌شده | TicketServiceImpl و TicketApi |
| paging و فیلتر user در service | انجام‌شده | findAll(page,size) و findByUser |
| API paging و فیلتر user | انجام‌نشده | /page کامنت است و GET collection فیلتر ندارد |
| کاربر موجود و وضعیت OPEN هنگام ساخت | انجام‌شده | loadUser و مقداردهی server |
| منع ویرایش/پیام برای تیکت CLOSED | انجام‌شده | checkNotClosed در مسیرهای اصلی |
| سرویس بستن تیکت | انجام‌شده | TicketService.close |
| API بستن تیکت | انجام‌نشده | مسیر `/tickets/{id}/close` وجود ندارد |
| API تو‌در‌توی پیام | انجام‌شده | GET/POST `/tickets/{ticketId}/messages`؛ ticket ناموجود 404 |
| تطبیق ticketId بدنه با مسیر | انجام‌شده | TicketMessagesApi اختلاف را رد می‌کند |
| تغییر وضعیت با پاسخ کاربر دیگر | انجام‌شده | addMessage وضعیت را IN_PROGRESS می‌کند؛ هنوز تست ندارد |
| خواندن پیام‌ها در transaction | انجام‌شده | findByTicket برای mapping Lazy، Transactional است |
| خواندن DTO تیکت در transaction | ناقص | TicketService خواندنی به user Lazy دست می‌زند ولی Transactional نیست |
| حذف عملیات عمومی قدیمی پیام | انجام‌نشده | impl هنوز create/update/delete/findPage دارد؛ interface جدید فقط دو عملیات اصلی دارد |
| پاک‌سازی API کامنت‌شده و servletها | انجام‌نشده | TicketMessageApi و servletهای قدیمی باقی‌اند |
| UI تیکت و پیام | انجام‌شده | فرم‌ها با قرارداد فعلی؛ پیام append-only |
| تست خودکار و بررسی HTTP | انجام‌نشده | هیچ تست این ماژول اجرا نشده و deploy انجام نشده است |

«انجام‌شده» به معنی وجود کد است، نه تأیید رفتار روی سرور.

## قرارداد فعلی و مسیرها

TicketRequest فعلاً subject، username، userId، status، createdAt و updatedAt می‌گیرد.
service هنگام ساخت status را OPEN می‌کند و هنگام update فقط subject را تغییر می‌دهد؛
بنابراین نمایش status در فرم، endpoint تغییر وضعیت محسوب نمی‌شود.
TicketDto نام‌های `userID` و `ticketStatus` دارد؛ UI این تفاوت را با source تطبیق داده است.

| مسیر پس از /api/v1 | عملیات |
|---|---|
| `/tickets` | GET همه / POST ایجاد |
| `/tickets/{id}` | GET / PUT / DELETE |
| `/tickets/{ticketId}/messages` | GET همهٔ پیام‌های تیکت / POST پیام |

GET صفحه، GET با userId و عملیات close هنوز در REST موجود نیستند.

## ترتیب ادامه

۱. Request تیکت را به subject/userId و Request پیام را به senderId/content محدود کنید؛
timestamp و وضعیت را server تعیین کند.
۲. paging، فیلتر user و endpoint بستن را به service موجود وصل کنید.
۳. خواندن Lazy را transactional و عملیات عمومی قدیمی پیام را حذف کنید.
۴. 404، تیکت بسته و تغییر IN_PROGRESS را تست و سپس از UI بررسی کنید.
در transaction برای entity managed، persist/merge دوباره لازم نیست.

## نتیجهٔ بررسی

کامپایل و package با `-DskipTests` در ۲۰۲۶/۱۰/۱۳ موفق شد.
تست tickets در suite وجود ندارد؛ validation، mapping Lazy و مجوزها روی سرور
در این نوبت بررسی نشده‌اند. فقط README تغییر کرده است.

</div>
