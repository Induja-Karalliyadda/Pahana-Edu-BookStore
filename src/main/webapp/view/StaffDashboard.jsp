<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, com.pahanaedu.model.Book, com.pahanaedu.model.Accessory, com.pahanaedu.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>POS | Pahana Edu Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
  <style>
    body { background: var(--light); }
    .pos-wrapper { display: grid; grid-template-columns: 1fr 380px; gap: 36px; margin-top: 35px; align-items: flex-start; }
    @media(max-width:900px){ .pos-wrapper { display:block; } .cart-panel { margin-top:32px; } }
    .cart-panel { background: var(--white); box-shadow:0 2px 6px rgba(0,0,0,.07); border-radius:12px; padding:18px 15px; min-height:320px; position:sticky; top:100px; }
    .cart-table td input { width: 56px; }
    .searchbox { margin-bottom: 1em; }
    .cart-header { font-size: 21px; color:var(--purple); margin-bottom:12px; }
    .cart-total-row { font-weight:bold; font-size: 18px; margin-top: 10px; padding-top: 10px; border-top: 2px solid var(--purple); display:flex; justify-content:space-between;}
    .table { width:100%; margin-bottom:18px; border-collapse: collapse; }
    .table th, .table td { padding: 8px 10px; border-bottom: 1px solid #eee; }
    .cart-panel .searchbox { margin-bottom: 14px; padding-bottom: 8px; border-bottom: 1px solid #eee; }
    .cart-panel #customerSearch, .cart-panel #customerSelect { width: 100%; display: block; margin-bottom:8px; padding: 8px; border: 1px solid #ddd; border-radius: 6px; }
    .cart-panel #btnNewCustomer { width:100%; margin-top:7px; }
    .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,.4); z-index:1000; justify-content:center; align-items:center; }
    .modal-overlay.show { display:flex; }
    .modal-content { background:#fff; padding:28px; border-radius:12px; width:420px; max-width:90%; box-shadow:0 4px 20px rgba(0,0,0,.15); }
    .modal-header { font-size:20px; color: var(--purple); margin-bottom: 20px; font-weight: 700; }
    .form-group { margin-bottom: 14px; }
    .form-group label { display:block; margin-bottom:5px; color:#555; font-size:14px; }
    .form-group input { width:100%; padding:10px; border:1px solid #ddd; border-radius:6px; font-size:14px; }
    .btn { cursor:pointer; }
    .btn-cancel { background:#f0f0f0; color:#333; border:none; padding:8px 12px; border-radius:8px; }
    .btn-cancel:hover { background:#e0e0e0; }
    .stock-low { color: #e74c3c; font-weight: bold; }
    .stock-ok { color: #27ae60; }
    .item-info { display:flex; gap:16px; flex-wrap:wrap; margin-bottom:10px; font-size:14px; }
  </style>
</head>
<body>
<header class="topbar">
  <div class="top-actions">
    <button id="toggleSidebar" aria-label="menu">&#9776;</button>
  </div>
  <div class="brand">Pahana Edu <span> POS</span></div>
  <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar">
</header>

 <!-- SIDEBAR -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
       <c:url var="dashboardUrl" value="/AdminPOSController"/>
      <c:url var="itemsBookUrl" value="/items"><c:param name="mainCategory" value="book"/></c:url>
      <c:url var="usersUrl" value="/users"/>
      <c:url var="staffUrl" value="/staff"/>
            <c:url var="logoutUrl" value="/logout"/>

      <a href="${dashboardUrl}">Dashboard</a>
      <a href="#">Orders</a>
      <a href="${itemsBookUrl}" class="active">Item</a>
      <a href="${usersUrl}">Users</a>

      <!-- Show Staff link only for Admin -->
      <c:if test="${sessionScope.role == 'admin'}">
        <a href="${staffUrl}">Staff</a>
      </c:if>

      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${logoutUrl}" class="logout">Logout</a>
    </nav>
  </aside>
<div class="overlay" id="overlay"></div>

<div class="main" id="main">
  <h1 style="color:var(--purple);margin-bottom:18px;">Point of Sale System</h1>

  <div class="item-info">
    <span>Total Books: <strong>${books.size()}</strong></span>
    <span>Total Accessories: <strong>${accessories.size()}</strong></span>
    <span>Total Customers: <strong>${customers.size()}</strong></span>
  </div>

  <div class="pos-wrapper">
    <div class="main-content">
      <h2 style="color:var(--purple);margin-top:6px;">📚 Books</h2>
      <table class="table">
        <thead>
          <tr><th>Title</th><th>Author</th><th>Stock</th><th>Price (Rs.)</th><th>Action</th></tr>
        </thead>
        <tbody>
        <c:forEach var="book" items="${books}">
          <tr>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td class="${book.stock <= book.minStock ? 'stock-low' : 'stock-ok'}">${book.stock}</td>
            <td>${book.sellingPrice}</td>
            <td>
              <button class="btn add-cart-btn"
                      data-type="book"
                      data-id="${book.bookId}"
                      data-name="${book.title}"
                      data-price="${book.sellingPrice}"
                      data-stock="${book.stock}"
                      ${book.stock <= 0 ? 'disabled' : ''}>
                ${book.stock <= 0 ? 'Out of Stock' : 'Add to Cart'}
              </button>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty books}">
          <tr><td colspan="5" style="text-align:center;">No books available</td></tr>
        </c:if>
        </tbody>
      </table>

      <h2 style="color:var(--purple);">✏️ Accessories</h2>
      <table class="table">
        <thead>
          <tr><th>Name</th><th>Category</th><th>Stock</th><th>Price (Rs.)</th><th>Action</th></tr>
        </thead>
        <tbody>
        <c:forEach var="acc" items="${accessories}">
          <tr>
            <td>${acc.name}</td>
            <td>${acc.category}</td>
            <td class="${acc.stock <= acc.minStock ? 'stock-low' : 'stock-ok'}">${acc.stock}</td>
            <td>${acc.sellingPrice}</td>
            <td>
              <button class="btn add-cart-btn"
                      data-type="accessory"
                      data-id="${acc.accessoryId}"
                      data-name="${acc.name}"
                      data-price="${acc.sellingPrice}"
                      data-stock="${acc.stock}"
                      ${acc.stock <= 0 ? 'disabled' : ''}>
                ${acc.stock <= 0 ? 'Out of Stock' : 'Add to Cart'}
              </button>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty accessories}">
          <tr><td colspan="5" style="text-align:center;">No accessories available</td></tr>
        </c:if>
        </tbody>
      </table>
    </div>

    <div class="cart-panel">
      <div class="cart-header">🛒 Shopping Cart</div>

      <div class="searchbox">
        <label style="font-size:14px;color:#666;margin-bottom:5px;display:block;">Customer Selection</label>
        <input id="customerSearch" type="text" placeholder="Search customer by name or code...">
        <select id="customerSelect">
          <option value="">-- Select Customer --</option>
          <c:forEach var="u" items="${customers}">
            <option value="${u.id}" data-code="${u.customerCode}">
              ${u.username} [${u.customerCode}]
            </option>
          </c:forEach>
        </select>
        <button id="btnNewCustomer" class="btn small">➕ Add New Customer</button>
      </div>

      <table class="table cart-table" id="cartTable">
        <thead>
          <tr><th>Item</th><th>Qty</th><th>Price</th><th>Total</th><th></th></tr>
        </thead>
        <tbody>
          <tr><td colspan="5" style="text-align:center;color:#999;">Cart is empty</td></tr>
        </tbody>
      </table>

      <div class="cart-total-row">
        <span>Total: Rs.</span>
        <span id="cartTotal">0.00</span>
      </div>

      <button class="btn primary" id="btnPlaceOrder" style="width:100%;margin-top:18px;">💳 Place Order</button>
      <button class="btn" id="btnClearCart" style="width:100%;margin-top:10px;">🗑️ Clear Cart</button>
      <button class="btn" id="btnInvoice" style="width:100%;margin-top:10px;display:none;">🧾 Generate Bill (PDF)</button>
    </div>
  </div>
</div>

<!-- Add Customer Modal -->
<div id="addCustomerModal" class="modal-overlay">
  <div class="modal-content">
    <div class="modal-header">Add New Customer</div>
    <form id="newCustomerForm" autocomplete="off">
      <div class="form-group">
        <label for="newCustomerName">Customer Name *</label>
        <input type="text" id="newCustomerName" required />
      </div>
      <div class="form-group">
        <label for="newCustomerAddress">Address</label>
        <input type="text" id="newCustomerAddress" />
      </div>
      <div class="form-group">
        <label for="newCustomerTelephone">Telephone</label>
        <input type="tel" id="newCustomerTelephone" />
      </div>
      <div class="form-group">
        <label for="newCustomerEmail">Email</label>
        <input type="email" id="newCustomerEmail" />
      </div>
      <div style="display:flex; gap:10px; justify-content:flex-end; margin-top:10px;">
        <button type="button" id="closeCustomerModal" class="btn btn-cancel">Cancel</button>
        <button type="submit" class="btn primary">Add Customer</button>
      </div>
    </form>
  </div>
</div>

<script>
  const BASE = '<c:out value="${pageContext.request.contextPath}"/>';

  // Sidebar
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('overlay');
  const main = document.getElementById('main');
  const toggleBtn = document.getElementById('toggleSidebar');
  function toggleSidebarFn(){
    if(!sidebar) return;
    sidebar.classList.toggle('open');
    overlay.classList.toggle('show');
    main.classList.toggle('shift');
  }
  toggleBtn && toggleBtn.addEventListener('click', toggleSidebarFn);
  overlay   && overlay.addEventListener('click', toggleSidebarFn);

  // Cart
  let cart = [];
  function updateCartTable() {
    const tbody = document.querySelector("#cartTable tbody");
    tbody.innerHTML = "";
    let total = 0;
    if (cart.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#999;">Cart is empty</td></tr>';
      document.getElementById('cartTotal').textContent = '0.00';
      return;
    }
    cart.forEach(function(item, idx) {
      var itemTotal = item.price * item.qty;
      total += itemTotal;
      tbody.innerHTML += ''
        + '<tr>'
        +   '<td>' + item.name + '</td>'
        +   '<td><input type="number" min="1" max="' + item.stock + '" value="' + item.qty + '" data-idx="' + idx + '" class="cart-qty-input"/></td>'
        +   '<td>' + item.price.toFixed(2) + '</td>'
        +   '<td>' + itemTotal.toFixed(2) + '</td>'
        +   '<td><button class="btn small remove-cart-btn" data-idx="' + idx + '">❌</button></td>'
        + '</tr>';
    });
    document.getElementById('cartTotal').textContent = total.toFixed(2);
  }

  document.addEventListener('click', function(e){
    if(e.target.classList.contains('add-cart-btn') && !e.target.disabled) {
      var btn = e.target, type = btn.dataset.type, id = btn.dataset.id, stock = parseInt(btn.dataset.stock, 10);
      if (stock <= 0) { alert('This item is out of stock!'); return; }
      var found = cart.find(function(i){ return i.type===type && i.id===id; });
      if(found) { if(found.qty < found.stock) found.qty++; else alert('Cannot add more than available stock!'); }
      else { cart.push({ type:type, id:id, name:btn.dataset.name, price:parseFloat(btn.dataset.price), qty:1, stock:stock }); }
      updateCartTable();
    }
    if(e.target.classList.contains('remove-cart-btn')) { cart.splice(e.target.dataset.idx, 1); updateCartTable(); }
  });

  document.addEventListener('input', function(e){
    if(e.target.classList.contains('cart-qty-input')) {
      var idx = e.target.dataset.idx, val = parseInt(e.target.value, 10);
      if(val < 1) val = 1; if(val > cart[idx].stock) val = cart[idx].stock;
      cart[idx].qty = val; updateCartTable();
    }
  });

  // Search customers (debounced)
  var searchTimer = null;
  document.getElementById('customerSearch').addEventListener('input', function(){
    var q = this.value.trim();
    clearTimeout(searchTimer);
    searchTimer = setTimeout(function () {
      fetch(BASE + '/CustomerSearchController?q=' + encodeURIComponent(q) + '&_=' + Date.now())
        .then(function(res){ if (!res.ok) throw new Error('HTTP ' + res.status); return res.json(); })
        .then(function(users){
          var sel = document.getElementById('customerSelect');
          sel.innerHTML = '<option value="">-- Select Customer --</option>';
          (users || []).forEach(function(u){
            var opt = document.createElement('option');
            opt.value = u.id; opt.dataset.code = u.customerCode;
            opt.textContent = u.username + ' [' + u.customerCode + ']';
            sel.appendChild(opt);
          });
        })
        .catch(function(err){ console.error('Search error:', err); });
    }, 250);
  });

  // === LIVE STOCK REFRESH (single version) ===
  function updateDOMStock(type, id, newStock, minStock){
    const btn = document.querySelector(`.add-cart-btn[data-type="${type}"][data-id="${id}"]`);
    if(!btn) return;

    btn.dataset.stock = String(newStock);

    const row = btn.closest('tr');
    if (row) {
      const stockCell = row.children[2]; // 3rd column
      if (stockCell) {
        stockCell.textContent = newStock;
        stockCell.classList.remove('stock-low','stock-ok');
        stockCell.classList.add(Number(newStock) <= Number(minStock || 0) ? 'stock-low' : 'stock-ok');
      }
    }

    if (Number(newStock) <= 0) { btn.disabled = true; btn.textContent = 'Out of Stock'; }
    else { btn.disabled = false; btn.textContent = 'Add to Cart'; }
  }

  function refreshStocks(){
    const url = BASE + '/ItemStockController?_=' + Date.now();
    fetch(url, { cache: 'no-store' })
      .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
      .then(data => {
        (data.books || []).forEach(b => updateDOMStock('book', String(b.id), Number(b.stock), Number(b.minStock)));
        (data.accessories || []).forEach(a => updateDOMStock('accessory', String(a.id), Number(a.stock), Number(a.minStock)));
      })
      .catch(err => console.error('[stocks] refresh error:', err));
  }

  // Auto-refresh every 30s and once on load
  document.addEventListener('DOMContentLoaded', function(){
    refreshStocks();
    updateCartTable();
  });
  setInterval(refreshStocks, 30000);

  // Place order -> show invoice button, open PDF, refresh stocks
  const btnInvoice = document.getElementById('btnInvoice');
  document.getElementById('btnPlaceOrder').addEventListener('click', function(){
    var customerSel = document.getElementById('customerSelect');
    if(cart.length == 0) { alert('Cart is empty! Please add items to cart.'); return; }
    if(!customerSel.value) { alert('Please select a customer or add a new one.'); return; }

    var customerId = customerSel.value;
    var customerCode = customerSel.selectedOptions[0].dataset.code;
    var customerName = customerSel.selectedOptions[0].textContent.split('[')[0].trim();
    var total = parseFloat(document.getElementById('cartTotal').textContent);

    if(!confirm('Place order for ' + customerName + '?\nTotal: Rs. ' + total.toFixed(2))) return;

    fetch(BASE + '/OrderController', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
        customerId: customerId,
        customerCode: customerCode,
        customerName: customerName,
        total: total,
        cart: cart.map(function(i){ return { type:i.type, id:i.id, name:i.name, qty:i.qty, price:i.price }; })
      })
    })
    .then(function(r){ return r.json(); })
    .then(function(res){
      if(res.status === 'ok'){
        // Enable Generate Bill button
        btnInvoice.dataset.orderId = res.orderId;
        btnInvoice.style.display = 'block';
        btnInvoice.onclick = function(){
          window.open(BASE + '/InvoicePdfController?orderId=' + btnInvoice.dataset.orderId, '_blank');
        };
        // Auto-open once
        window.open(BASE + '/InvoicePdfController?orderId=' + res.orderId, '_blank');

        alert('Order placed successfully!\nInvoice Number: ' + res.invoiceNumber);
        cart = []; updateCartTable();

        // refresh stocks immediately
        refreshStocks();
      } else {
        alert('Error placing order: ' + (res.message || 'Unknown error'));
      }
    })
    .catch(function(err){
      console.error('Order error:', err);
      alert('Error placing order. Please try again.');
    });
  });

  // Clear cart
  document.getElementById('btnClearCart').addEventListener('click', function() {
    if(cart.length > 0 && confirm('Clear all items from cart?')) { cart = []; updateCartTable(); }
  });
</script>
</body>
</html>




