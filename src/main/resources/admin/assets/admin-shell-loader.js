(async () => {
  try {
    const response = await fetch("/layout/fragments/sidebar.html");
    const html = await response.text();
    const doc = new DOMParser().parseFromString(html, "text/html");
    const sidebar = doc.querySelector("aside");
    const shellScript = doc.querySelector("script[data-admin-shell]");
    if (sidebar) {
      document.body.prepend(sidebar);
    }
    if (shellScript) {
      new Function(shellScript.textContent)();
      if (typeof window.initAdminShell === "function") {
        window.initAdminShell();
      }
    }
  } catch (e) {
    console.error("Cannot load sidebar shell", e);
  }
})();
