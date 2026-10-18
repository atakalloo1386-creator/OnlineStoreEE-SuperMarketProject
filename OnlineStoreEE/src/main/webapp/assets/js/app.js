import { api, setCredentials, clearCredentials } from "./core/api.js";
import { element, button, message, formFields, readForm, displayValue, safeJson } from "./core/ui.js";
import { groups, resources } from "./modules.js";

const byId = identifier => document.getElementById(identifier);
const editor = byId("editor");
const state = { resource: null, params: {}, rows: [], page: 0, pages: 0, total: 0, paged: false, extraPath: null };
let loadNumber = 0;
let editorBusy = false;

function errorMessage(target, error) {
    message(target, error.message || "عملیات انجام نشد.", "danger", error.details || []);
}

function routePath(template, values = state.params) {
    return template.replace(/\{(\w+)\}/g, (placeholder, name) => {
        if (values[name] === undefined || values[name] === null) throw new Error("شناسهٔ مورد نیاز را مشخص کنید.");
        return encodeURIComponent(values[name]);
    });
}

function scopeReady(resource = state.resource, params = state.params) {
    return (resource.scope || []).every(field => params[field.name] !== undefined && params[field.name] !== null);
}

function renderNavigation() {
    const navigation = byId("navigation");
    groups.forEach(([group, label]) => {
        navigation.append(element("div", { class: "nav-group" }, label));
        resources.filter(resource => resource.group === group).forEach(resource => {
            navigation.append(element("a", { href: `#${resource.id}`, "data-resource": resource.id }, resource.title));
        });
    });
}

function renderFilters() {
    const resource = state.resource;
    const fields = [...(resource.scope || []), ...(resource.filters || [])];
    const filters = byId("filters");
    filters.replaceChildren();
    if (fields.length) {
        filters.append(formFields(fields));
        const controls = element("div", { class: "col-12 d-flex gap-2" }, element("button", { type: "submit", class: "btn btn-sm btn-outline-primary" }, "نمایش"));
        controls.append(button("پاک کردن فیلتر", "btn btn-sm btn-outline-secondary", () => {
            filters.reset();
            state.params = {};
            state.extraPath = null;
            state.page = 0;
            loadRows();
        }));
        filters.append(controls);
    }
    (resource.extraLists || []).forEach(action => {
        filters.append(element("div", { class: "col-auto" }, button(action.label, "btn btn-sm btn-outline-primary", () => {
            state.extraPath = action.path;
            state.page = 0;
            loadRows();
        })));
    });
    filters.onsubmit = event => {
        event.preventDefault();
        try {
            const params = readForm(filters, fields);
            if (resource.id === "attachments" && Boolean(params.ownerType) !== Boolean(params.ownerId)) throw new Error("نوع مالک و شناسهٔ مالک را با هم مشخص کنید.");
            if (resource.id === "departments" && params.branchId && params.userId) throw new Error("یکی از فیلترهای شعبه یا کارمند را انتخاب کنید.");
            state.params = params;
            state.extraPath = null;
            state.page = 0;
            loadRows();
        } catch (error) {
            errorMessage(byId("message"), error);
        }
    };
}

function selectResource() {
    const resource = resources.find(entry => entry.id === location.hash.slice(1)) || resources[0];
    Object.assign(state, { resource, params: {}, rows: [], page: 0, pages: 0, total: 0, paged: false, extraPath: null });
    byId("page-title").textContent = resource.title;
    byId("page-description").textContent = resource.description || "نمایش و مدیریت اطلاعات این بخش";
    byId("module-note").hidden = !resource.notice;
    byId("module-note").textContent = resource.notice || "";
    byId("new-record").textContent = resource.readOnly ? "ساختار فرم" : resource.single ? "ثبت پروفایل" : "افزودن";
    byId("search").value = "";
    document.querySelectorAll("[data-resource]").forEach(link => {
        if (link.dataset.resource === resource.id) link.setAttribute("aria-current", "page");
        else link.removeAttribute("aria-current");
    });
    renderFilters();
    loadRows();
}

async function loadRows() {
    const currentLoad = ++loadNumber;
    const resource = state.resource;
    message(byId("message"), "");
    state.rows = [];
    state.total = 0;
    state.pages = 0;
    state.paged = Boolean(resource.paged && !state.extraPath && !Object.values(state.params).some(value => value !== null && value !== ""));
    byId("page-size-label").hidden = !resource.paged;
    byId("loading").hidden = true;
    byId("refresh").disabled = false;
    renderTable();
    if (!scopeReady()) {
        message(byId("message"), "شناسهٔ بالای صفحه را وارد کنید و «نمایش» را بزنید.", "info");
        return false;
    }
    let path = routePath(resource.path);
    if (state.extraPath) path = state.extraPath;
    else if (state.paged) path += "/page";
    else if (resource.filterPath && Object.values(state.params).some(value => value !== null)) path = routePath(resource.filterPath);
    const query = state.paged ? { page: state.page, size: Number(byId("page-size").value) } : state.params;
    byId("loading").hidden = false;
    byId("refresh").disabled = true;
    try {
        const data = await api.get(path, query);
        if (currentLoad !== loadNumber) return;
        if (state.paged && data && Array.isArray(data.content)) {
            state.rows = data.content;
            state.pages = data.totalPages;
            state.total = data.totalElements;
        } else {
            state.paged = false;
            state.rows = Array.isArray(data) ? data : data ? [data] : [];
            state.total = state.rows.length;
        }
        return true;
    } catch (error) {
        if (currentLoad !== loadNumber) return;
        if (resource.single && error.status === 404) message(byId("message"), "پروفایلی برای این شناسه دریافت نشد؛ در صورت وجود کاربر می‌توانید پروفایل ثبت کنید.", "info");
        else errorMessage(byId("message"), error);
        return false;
    } finally {
        if (currentLoad === loadNumber) {
            byId("loading").hidden = true;
            byId("refresh").disabled = false;
            renderTable();
        }
    }
}

function renderTable() {
    const resource = state.resource;
    const heading = element("tr", {}, resource.columns.map(([, label]) => element("th", { scope: "col" }, label)), element("th", { scope: "col" }, "عملیات"));
    byId("table-head").replaceChildren(heading);
    const search = byId("search").value.trim().toLocaleLowerCase("fa");
    const rows = state.rows.filter(record => resource.columns.some(([key]) => displayValue(record[key]).toLocaleLowerCase("fa").includes(search)));
    byId("table-body").replaceChildren();
    rows.forEach(record => {
        const actions = element("div", { class: "row-actions" });
        actions.append(button("جزئیات", "btn btn-sm btn-outline-secondary", () => showDetails(record)));
        if (!resource.readOnly && resource.canEdit !== false) actions.append(button("ویرایش", "btn btn-sm btn-outline-primary", () => openEditor(record)));
        if (!resource.readOnly && resource.canDelete !== false) actions.append(button("حذف", "btn btn-sm btn-outline-danger", event => deleteRecord(record, event.currentTarget)));
        (resource.actions || []).forEach(action => actions.append(button(action.label, "btn btn-sm btn-outline-secondary", event => runAction(record, action, event.currentTarget))));
        if (resource.files) {
            actions.append(button("آپلود", "btn btn-sm btn-outline-primary", () => openUpload(record)));
            if (record.fileSize !== null && record.fileSize !== undefined) actions.append(button("دانلود", "btn btn-sm btn-outline-success", event => downloadFile(record, event.currentTarget)));
        }
        const cells = resource.columns.map(([key]) => element("td", {}, element("span", { class: "cell-value" }, displayValue(record[key]))));
        byId("table-body").append(element("tr", {}, cells, element("td", {}, actions)));
    });
    if (!rows.length) byId("table-body").append(element("tr", {}, element("td", { colspan: resource.columns.length + 1, class: "text-center text-secondary py-4" }, search ? "ردیفی مطابق جست‌وجو پیدا نشد." : "ردیفی از API دریافت نشده است.")));
    byId("row-count").textContent = `${rows.length} ردیف نمایش داده شده · ${state.total} رکورد`;
    byId("pagination").hidden = !state.paged;
    byId("page-info").textContent = state.pages ? `صفحهٔ ${state.page + 1} از ${state.pages}` : "صفحه‌ای وجود ندارد";
    byId("previous-page").disabled = state.page === 0;
    byId("next-page").disabled = state.page + 1 >= state.pages;
}

function showDialog(title) {
    if (editorBusy) return false;
    byId("editor-title").textContent = title;
    byId("editor-body").replaceChildren();
    message(byId("editor-message"), "");
    if (!editor.open) editor.showModal();
    return true;
}

function showDetails(record) {
    if (showDialog("جزئیات رکورد")) byId("editor-body").append(element("pre", {}, safeJson(record)));
}

function mutationPath(resource, record, params) {
    const path = routePath(resource.path, params);
    return resource.single || !record ? path : `${path}/${encodeURIComponent(record.id)}`;
}

async function openEditor(record = null) {
    const resource = state.resource;
    const params = { ...state.params };
    if (!scopeReady(resource, params)) {
        message(byId("message"), "ابتدا شناسهٔ بالای صفحه را مشخص کنید.", "warning");
        return;
    }
    if (!showDialog(`${resource.readOnly ? "ساختار فرم" : record ? "ویرایش" : "افزودن"} ${resource.title}`)) return;
    let values = record ? { ...record } : {};
    const fields = resource.fields;
    if (resource.id === "orders" && record) {
        values.customer = record.customer ? { id: record.customer.id } : null;
        values.orderItems = (record.orderItems || []).map(item => ({ product: { id: item.product?.id }, quantity: item.quantity, unitPrice: item.unitPrice }));
    }
    if (resource.id === "users" && record) {
        editorBusy = true;
        try {
            const roles = await api.get("roles");
            values.roleIds = (record.roles || []).map(name => {
                const role = roles.find(entry => entry.name === name);
                if (!role) throw new Error("شناسهٔ یکی از نقش‌ها پیدا نشد؛ ابتدا بخش نقش‌ها را بررسی کنید.");
                return role.id;
            });
        } catch (error) {
            errorMessage(byId("editor-message"), error);
            return;
        } finally {
            editorBusy = false;
        }
    }
    const form = element("form");
    const fieldset = element("fieldset", { disabled: resource.readOnly }, formFields(fields, values));
    form.append(fieldset);
    if (resource.readOnly) form.append(element("p", { class: "small text-secondary mt-3" }, resource.notice));
    else form.append(element("button", { type: "submit", class: "btn btn-primary mt-3" }, "ذخیره"));
    form.onsubmit = async event => {
        event.preventDefault();
        if (resource.readOnly || editorBusy) return;
        try {
            const body = readForm(form, fields);
            if (resource.id === "orders" && (!Array.isArray(body.orderItems) || !body.orderItems.length || !body.customer?.id)) throw new Error("مشتری باید شناسه داشته باشد و اقلام سفارش یک آرایهٔ غیرخالی باشند.");
            if (resource.id === "ticket-messages") body.ticketId = params.ticketId;
            editorBusy = true;
            fieldset.disabled = true;
            form.querySelector('[type="submit"]').disabled = true;
            const method = resource.saveMethod || (record ? "PUT" : "POST");
            await api[method.toLowerCase()](mutationPath(resource, record, params), body);
            editor.close();
            if (state.resource === resource) {
                const refreshed = await loadRows();
                if (refreshed) message(byId("message"), "اطلاعات ذخیره شد.", "success");
            }
        } catch (error) {
            errorMessage(byId("editor-message"), error);
        } finally {
            editorBusy = false;
            fieldset.disabled = false;
            const submit = form.querySelector('[type="submit"]');
            if (submit) submit.disabled = false;
        }
    };
    byId("editor-body").append(form);
}

async function deleteRecord(record, control) {
    if (!confirm(`رکورد ${record.id} حذف شود؟`)) return;
    const resource = state.resource;
    const path = mutationPath(resource, record, state.params);
    control.disabled = true;
    try {
        await api.delete(path);
        if (state.resource === resource) {
            if (state.rows.length === 1 && state.page > 0) state.page -= 1;
            await loadRows();
        }
    } catch (error) {
        if (state.resource === resource) errorMessage(byId("message"), error);
    } finally {
        control.disabled = false;
    }
}

async function runAction(record, action, control) {
    const resource = state.resource;
    const params = { ...state.params };
    if (action.input) {
        const entered = prompt(action.input.label);
        if (entered === null) return;
        const identifier = Number(entered);
        if (!Number.isSafeInteger(identifier) || identifier < 1) {
            message(byId("message"), "شناسه باید عدد مثبت باشد.");
            return;
        }
        params[action.input.name] = identifier;
    }
    if (action.method !== "GET" && !confirm(`${action.label} برای رکورد ${record.id} انجام شود؟`)) return;
    control.disabled = true;
    try {
        const path = `${routePath(resource.path, params)}/${encodeURIComponent(record.id)}/${routePath(action.path, params)}`;
        const result = await api[action.method.toLowerCase()](path);
        if (state.resource !== resource) return;
        if (action.method === "GET") {
            if (showDialog(action.label)) byId("editor-body").append(element("pre", {}, safeJson(result)));
        } else await loadRows();
    } catch (error) {
        if (state.resource === resource) errorMessage(byId("message"), error);
    } finally {
        control.disabled = false;
    }
}

function openUpload(record) {
    if (!showDialog(`آپلود فایل برای ${record.title}`)) return;
    const form = element("form");
    const fileInput = element("input", { type: "file", id: "attachment-file", class: "form-control", required: true });
    const submit = element("button", { type: "submit", class: "btn btn-primary mt-3" }, "آپلود");
    form.append(element("label", { for: "attachment-file", class: "form-label" }, `نام ثبت‌شده در متادیتا: ${record.fileName}`), fileInput, submit);
    byId("editor-body").append(form);
    form.onsubmit = async event => {
        event.preventDefault();
        if (editorBusy || !fileInput.files.length) return;
        editorBusy = true;
        submit.disabled = true;
        try {
            await api.upload(`attachments/${encodeURIComponent(record.id)}/content`, fileInput.files[0]);
            editor.close();
            if (state.resource.id === "attachments") await loadRows();
        } catch (error) {
            errorMessage(byId("editor-message"), error);
        } finally {
            editorBusy = false;
            submit.disabled = false;
        }
    };
}

async function downloadFile(record, control) {
    control.disabled = true;
    try {
        const blob = await api.download(`attachments/${encodeURIComponent(record.id)}/content`);
        const url = URL.createObjectURL(blob);
        const link = element("a", { href: url, download: record.fileName });
        document.body.append(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
    } catch (error) {
        errorMessage(byId("message"), error);
    } finally {
        control.disabled = false;
    }
}

byId("credentials-form").onsubmit = event => {
    event.preventDefault();
    const form = event.currentTarget;
    const username = form.elements.username.value.trim();
    if (username.includes(":")) {
        message(byId("message"), "نام کاربری نمی‌تواند شامل : باشد.");
        return;
    }
    setCredentials(username, form.elements.password.value);
    form.reset();
    byId("auth-status").textContent = `درخواست‌ها با اطلاعات ${username} ارسال می‌شوند؛ اعتبارسنجی با سرور است.`;
    loadRows();
};
byId("clear-credentials").onclick = () => {
    clearCredentials();
    byId("credentials-form").reset();
    byId("auth-status").textContent = "اطلاعات دسترسی پاک شد.";
    loadRows();
};
byId("close-editor").onclick = () => { if (!editorBusy) editor.close(); };
editor.addEventListener("cancel", event => { if (editorBusy) event.preventDefault(); });
byId("refresh").onclick = () => { state.extraPath = null; loadRows(); };
byId("new-record").onclick = () => openEditor();
byId("search").oninput = renderTable;
byId("page-size").onchange = () => { state.page = 0; loadRows(); };
byId("previous-page").onclick = () => { if (state.page > 0) { state.page -= 1; loadRows(); } };
byId("next-page").onclick = () => { if (state.page + 1 < state.pages) { state.page += 1; loadRows(); } };
window.addEventListener("hashchange", selectResource);
renderNavigation();
selectResource();
