export function element(tag, attributes = {}, ...children) {
    const node = document.createElement(tag);
    Object.entries(attributes).forEach(([name, value]) => {
        if (value !== null && value !== undefined && value !== false) node.setAttribute(name, value === true ? "" : String(value));
    });
    children.flat().forEach(child => {
        if (child !== null && child !== undefined) node.append(child instanceof Node ? child : document.createTextNode(String(child)));
    });
    return node;
}

export function button(label, classes, handler) {
    const node = element("button", { type: "button", class: classes }, label);
    node.addEventListener("click", handler);
    return node;
}

export function message(target, text, type = "danger", details = []) {
    target.replaceChildren();
    if (!text) return;
    const alert = element("div", { class: `alert alert-${type} small mb-3` }, text);
    if (details.length) alert.append(element("ul", { class: "mb-0 mt-2" }, details.map(detail => element("li", {}, detail))));
    target.append(alert);
}

export function formFields(fields, values = {}) {
    const container = element("div", { class: "row g-3" });
    fields.forEach(field => {
        const value = values[field.source || field.name] ?? field.default ?? "";
        let input;
        if (field.type === "boolean") {
            input = element("select", { name: field.name, class: "form-select" },
                element("option", { value: "true" }, "بله"), element("option", { value: "false" }, "خیر"));
            input.value = value === false ? "false" : "true";
        } else if (field.options) {
            input = element("select", { name: field.name, class: "form-select", required: field.required });
            if (!field.required) input.append(element("option", { value: "" }, "انتخاب نشده"));
            field.options.forEach(option => input.append(element("option", { value: option }, option)));
            input.value = value || (field.required ? field.options[0] : "");
        } else if (["textarea", "json"].includes(field.type)) {
            input = element("textarea", { name: field.name, class: `form-control ${field.type === "json" ? "code-input" : ""}`, rows: field.type === "json" ? 5 : 3 });
            input.value = field.type === "json" && value !== "" ? JSON.stringify(value, null, 2) : value;
        } else {
            const type = field.type === "ids" ? "text" : field.type || "text";
            input = element("input", { name: field.name, class: "form-control", type, min: field.min, max: field.max, step: field.type === "datetime-local" ? "any" : field.step, minlength: field.minLength, maxlength: field.maxLength, autocomplete: field.type === "password" ? "new-password" : "off" });
            input.value = field.type === "ids" && Array.isArray(value) ? value.join(", ") : value;
            if (["number", "email", "date", "datetime-local", "ids", "password"].includes(field.type)) input.dir = "ltr";
        }
        input.required = Boolean(field.required);
        if (field.maxLength) input.maxLength = field.maxLength;
        const wrapper = element("div", { class: field.type === "json" || field.type === "textarea" ? "col-12" : "col-md-6" },
            element("label", { class: "d-block" }, element("span", { class: "form-label d-block" }, field.label, field.required ? " *" : ""), input));
        if (field.help) wrapper.append(element("div", { class: "form-text" }, field.help));
        container.append(wrapper);
    });
    return container;
}

export function readForm(form, fields) {
    return Object.fromEntries(fields.map(field => {
        const raw = form.elements.namedItem(field.name).value.trim();
        let value = raw || null;
        if (field.type === "boolean") value = raw === "true";
        if (raw && field.type === "number") {
            value = Number(raw);
            if (!Number.isFinite(value) || (field.step !== "any" && !Number.isSafeInteger(value))) throw new Error(`${field.label}: عدد معتبر وارد کنید.`);
        }
        if (field.type === "ids") {
            value = raw ? raw.split(/[,،\s]+/).map(identifier => Number(identifier)) : [];
            if (value.some(identifier => !Number.isSafeInteger(identifier) || identifier < 1)) throw new Error(`${field.label}: شناسه‌ها باید عدد مثبت باشند.`);
        }
        if (raw && field.type === "json") {
            try { value = JSON.parse(raw); } catch { throw new Error(`${field.label}: JSON معتبر وارد کنید.`); }
        }
        if (raw && field.type === "datetime-local" && raw.length === 16) value = `${raw}:00`;
        return [field.name, value];
    }));
}

export function displayValue(value) {
    if (value === null || value === undefined) return "—";
    if (typeof value === "boolean") return value ? "بله" : "خیر";
    if (Array.isArray(value)) return value.map(item => item && typeof item === "object" ? item.name || item.id : item ?? "—").join("، ");
    if (typeof value === "object") return value.name || value.username || value.title || value.id || "اطلاعات مرتبط";
    return String(value);
}

export function safeJson(value) {
    return JSON.stringify(value, (key, entry) => ["password", "storedName", "token"].includes(key) ? undefined : entry, 2);
}
