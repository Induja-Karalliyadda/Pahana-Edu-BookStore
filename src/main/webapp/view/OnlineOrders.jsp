<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Online Orders | Pahana Edu Admin</title>

  <!-- Your base CSS (keep this) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>

  <!-- THEME overrides: #8e44ad + #ecf0f1 -->
  <style>
    :root{
      --purple:#8e44ad;
      --light:#ecf0f1;
      --white:#ffffff;
      --text:#2c3e50;
      --pink:#8e44ad; /* ensure brand span uses purple too */
    }
    body{ background:var(--light); color:var(--text); }

    .topbar{ background:var(--white); border-bottom:3px solid rgba(142,68,173,.12); }
    .brand{ font-weight:800; color:var(--purple); }
    .brand span{ color:var(--pink); }
    .avatar{ width:36px; height:36px; border-radius:999px; }

    .sidebar{ position:fixed; top:60px; left:0; width:240px; height:calc(100% - 60px);
              background:var(--white); box-shadow:0 2px 6px rgba(0,0,0,.07);
              transform:translateX(-260px); transition:transform .25s ease; z-index:900; }
    .sidebar.open{ transform:translateX(0); }
    .menu-title{ padding:14px 16px; font-weight:700; color:#666; }
    .sidebar nav a{ display:block; padding:12px 16px; color:#444; text-decoration:none; border-left:3px solid transparent; }
    .sidebar nav a:hover{ background:rgba(142,68,173,.08); }
    .sidebar nav a.active{ background:rgba(142,68,173,.12); color:var(--purple); border-left-color:var(--purple); }
    .sidebar nav a.logout{ color:#b00020; }

    .overlay{ position:fixed; inset:0; background:rgba(0,0,0,.25); display:none; z-index:800; }
    .overlay.show{ display:block; }

    .main{ transition:transform .25s ease; padding:18px; }
    .main.shift{ transform:translateX(240px); }

    .page-header{ display:flex; justify-content:space-between; align-items:center; margin:20px 0 10px; }
    .filters{ display:flex; gap:10px; flex-wrap:wrap; align-items:center; }
    .filters input, .filters select{
      padding:8px 10px; border:1px solid #ddd; border-radius:8px; background:#fff; color:#333;
    }
    .filters input:focus, .filters select:focus, .status-select:focus{
      outline:2px solid rgba(142,68,173,.35);
      box-shadow:0 0 0 3px rgba(142,68,173,.12);
      border-color:rgba(142,68,173,.4);
    }

    .orders-table{ width:100%; border-collapse:collapse; background:var(--white);
                   box-shadow:0 2px 6px rgba(0,0,0,.07); border-radius:12px; overflow:hidden; }
    .orders-table th, .orders-table td{ padding:10px 12px; border-bottom:1px solid #eee; text-align:left; }
    .orders-table th{ background:var(--light); }

    .badge{ display:inline-block; padding:4px 10px; border-radius:999px; font-size:12px; font-weight:700; letter-spacing:.2px; }
    .badge.pending{   background:#fff1d6; color:#b77800; }
    .badge.paid{      background:#dff7df; color:#1d7f2b; }
    .badge.cancelled{ background:#ffe0e0; color:#b00020; }

    .btn{ border:none; background:var(--purple); color:#fff; padding:8px 12px; border-radius:8px; cursor:pointer; }
    .btn:hover{ filter:brightness(.96); }
    .btn.small{ padding:6px 10px; font-size:13px; }
    .btn.ghost{ background:rgba(142,68,173,.12); color:var(--purple); }
    .btn.warn{  background:#fff3cd; color:#8a6d3b; }
    .btn:disabled{ opacity:.6; cursor:not-allowed; }

    .table-actions{ display:flex; gap:8px; align-items:center; }
    .status-select{ padding:6px 8px; border:1px solid #ddd; border-radius:8px; background:#fff; }

    .empty{ text-align:center; color:#888; background:#fff; padding:22px; border-radius:12px; box-shadow:0 2px 6px rgba(0,0,0,.07); }

    /* Modal */
    .modal-overlay{ display:none; position:fixed; inset:0; background:rgba(0,0,0,.4); z-index:1000; justify-content:center; align-items:center; }
    .modal-overlay.show{ display:flex; }
    .modal-content{ background:#fff; padding:24px; border-radius:12px; width:720px; max-width:95%; box-shadow:0 8px 30px rgba(0,0,0,.2); }
    .modal-header{ display:flex; justify-content:space-between; align-items:center; margin-bottom:14px; }
    .modal-title{ font-size:20px; color:var(--purple); font-weight:800; }
    .close-x{ background:transparent; border:none; font-size:22px; cursor:pointer; }
    .items-table{ width:100%; border-collapse:collapse; }
    .items-table th, .items-table td{ padding:8px 10px; border-bottom:1px solid #eee; text-align:left; }
    .totals{ display:flex; justify-content:flex-end; gap:18px; margin-top:10px; font-weight:700; }
  </style>
</head>
<body>
  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions"><button id="toggleSidebar" aria-label="menu">&#9776;</button></div>
    <div class="brand">Pahana Edu <span>POS</span></div>
    <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar"/>
  </header>

  <!-- SIDEBAR -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <c:url var="dashboardUrl"     value="/AdminPOSController"/>
      <c:url var="itemsBookUrl"     value="/items"><c:param name="mainCategory" value="book"/></c:url>
      <c:url var="usersUrl"         value="/users"/>
      <c:url var="staffUrl"         value="/staff"/>
      <c:url var="logoutUrl"        value="/logout"/>
      <c:url var="onlineOrdersUrl"  value="/online-orders"/>

      <a href="${dashboardUrl}">Dashboard</a>
      <a href="${onlineOrdersUrl}" class="active">Orders</a>
      <a href="${itemsBookUrl}">Item</a>
      <a href="${usersUrl}">Users</a>
      <c:if test="${sessionScope.role == 'admin'}"><a href="${staffUrl}">Staff</a></c:if>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${logoutUrl}" class="logout">Logout</a>
    </nav>
  </aside>

  <div class="overlay" id="overlay"></div>

  <!-- MAIN -->
  <main class="main" id="main">
    <div class="page-header">
      <h1 style="color:var(--purple);">Online Orders</h1>
      <div class="filters">
        <input id="searchBox" type="text" placeholder="Search customer, email, phone..." />
        <select id="statusFilter">
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="PAID">Paid</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>
    </div>

    <c:choose>
      <c:when test="${not empty orders}">
        <table class="orders-table" id="ordersTable">
          <thead>
            <tr>
              <th>ID</th>
              <th>Customer</th>
              <th>Phone</th>
              <th>Email</th>
              <th>Address</th>
              <th>Total (Rs.)</th>
              <th>Status</th>
              <th>Created</th>
              <th style="width:220px;">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="o" items="${orders}">
              <tr data-order-id="${o.id}">
                <td>${o.id}</td>
                <td>${o.customerName}</td>
                <td>${o.phone}</td>
                <td>${o.email}</td>
                <td>${o.address}</td>
                <td><fmt:formatNumber value="${o.totalAmount}" type="number" minFractionDigits="2"/></td>
                <td>
                  <span class="badge ${o.status == 'PAID' ? 'paid' : (o.status == 'CANCELLED' ? 'cancelled' : 'pending')}" data-badge>${o.status}</span>
                </td>
                <td>${o.createdAt}</td>
                <td class="table-actions">
                  <button class="btn small ghost" data-view>View</button>
                  <select class="status-select" data-status>
                    <option value="PENDING"   ${o.status == 'PENDING'   ? 'selected' : ''}>PENDING</option>
                    <option value="PAID"      ${o.status == 'PAID'      ? 'selected' : ''}>PAID</option>
                    <option value="CANCELLED" ${o.status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                  </select>
                  <button class="btn small warn" data-invoice style="display:${o.status == 'PAID' ? 'inline-block' : 'none'};">Invoice</button>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:when>
      <c:otherwise>
        <div class="empty">No online orders found.</div>
      </c:otherwise>
    </c:choose>
  </main>

  <!-- Items Modal -->
  <div id="itemsModal" class="modal-overlay" role="dialog" aria-modal="true">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">Order <span id="mOrderId"></span> Items</div>
        <button class="close-x" id="closeItems">&times;</button>
      </div>
      <div id="orderMeta" style="margin-bottom:8px;color:#666;font-size:14px;"></div>
      <table class="items-table" id="itemsTable">
        <thead>
          <tr>
            <th>#</th>
            <th>Type</th>
            <th>Item</th>
            <th>Qty</th>
            <th>Unit (Rs.)</th>
            <th>Total (Rs.)</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
      <div class="totals"><div>Grand Total (Rs.):</div><div id="grandTotal">0.00</div></div>
    </div>
  </div>

  <script>
    const BASE = '<c:out value="${pageContext.request.contextPath}"/>';

    // Sidebar toggle
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('overlay');
    var main = document.getElementById('main');
    var toggleBtn = document.getElementById('toggleSidebar');
    function toggleSidebarFn(){ if(!sidebar) return; sidebar.classList.toggle('open'); overlay.classList.toggle('show'); main.classList.toggle('shift'); }
    if (toggleBtn) toggleBtn.addEventListener('click', toggleSidebarFn);
    if (overlay)   overlay.addEventListener('click', toggleSidebarFn);

    // Search + filter (client-side)
    var searchBox    = document.getElementById('searchBox');
    var statusFilter = document.getElementById('statusFilter');
    var ordersTable  = document.getElementById('ordersTable');

    function matches(row, q, status){
      var cells = Array.prototype.map.call(row.children, function(td){ return (td.textContent||'').toLowerCase(); });
      var textOk = !q || cells.some(function(t){ return t.indexOf(q) !== -1; });
      var badgeEl = row.querySelector('[data-badge]');
      var statusText = badgeEl ? badgeEl.textContent : '';
      var statusOk = !status || statusText === status;
      return textOk && statusOk;
    }
    function applyFilters(){
      if(!ordersTable) return;
      var q = (searchBox.value||'').toLowerCase().trim();
      var s = (statusFilter.value||'').toUpperCase();
      var rows = ordersTable.querySelectorAll('tbody tr');
      Array.prototype.forEach.call(rows, function(r){ r.style.display = matches(r, q, s) ? '' : 'none'; });
    }
    if (searchBox)    searchBox.addEventListener('input', applyFilters);
    if (statusFilter) statusFilter.addEventListener('change', applyFilters);

    // Modal helpers
    var itemsModal = document.getElementById('itemsModal');
    var closeItems = document.getElementById('closeItems');
    var mOrderId   = document.getElementById('mOrderId');
    var itemsTbody = document.querySelector('#itemsTable tbody');
    var grandTotal = document.getElementById('grandTotal');
    var orderMeta  = document.getElementById('orderMeta');

    function openItemsModal(orderId, meta){
      mOrderId.textContent = '#' + orderId;
      orderMeta.textContent = meta || '';
      itemsModal.classList.add('show');
    }
    function closeItemsModal(){ itemsModal.classList.remove('show'); itemsTbody.innerHTML=''; grandTotal.textContent='0.00'; }
    if (closeItems) closeItems.addEventListener('click', closeItemsModal);
    if (itemsModal) itemsModal.addEventListener('click', function(e){ if(e.target === itemsModal) closeItemsModal(); });

    // Simple JSON fetcher
    function fetchJSON(url, options){
      return fetch(url, options).then(function(res){ if(!res.ok) throw new Error('HTTP '+res.status); return res.json(); });
    }
    function badgeClass(status){
      if (status === 'PAID') return 'paid';
      if (status === 'CANCELLED') return 'cancelled';
      return 'pending';
    }

    // Row actions
    document.addEventListener('click', function(e){
      var row = e.target.closest && e.target.closest('tr[data-order-id]');
      if(!row) return;
      var orderId = row.getAttribute('data-order-id');

      // View items
      if (e.target.hasAttribute('data-view')) {
        var url = BASE + '/api/online-orders?action=items&orderId=' + encodeURIComponent(orderId) + '&_=' + Date.now();
        fetchJSON(url).then(function(data){
          itemsTbody.innerHTML = '';
          var i = 1, total = 0;
          (data.items || []).forEach(function(it){
            var qty  = Number(it.quantity) || 0;
            var unit = Number(it.unit_price) || 0;
            var line = qty * unit;
            total += line;
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + (i++) + '</td>'
              + '<td>' + it.item_type + '</td>'
              + '<td>' + it.item_name + '</td>'
              + '<td>' + qty + '</td>'
              + '<td>' + unit.toFixed(2) + '</td>'
              + '<td>' + line.toFixed(2) + '</td>';
            itemsTbody.appendChild(tr);
          });
          grandTotal.textContent = total.toFixed(2);
          var meta = row.children[1].textContent + ' • ' + row.children[2].textContent + ' • ' + row.children[3].textContent;
          openItemsModal(orderId, meta);
        }).catch(function(err){
          console.error(err); alert('Failed to load order items.');
        });
      }

      // Invoice (only meaningful when PAID)
      if (e.target.hasAttribute('data-invoice')) {
        var invUrl = BASE + '/InvoicePdfController?orderId=' + encodeURIComponent(orderId);
        window.open(invUrl, '_blank');
      }
    });

    // Status change
    document.addEventListener('change', function(e){
      if(!e.target.matches('[data-status]')) return;
      var select = e.target;
      var row = select.closest('tr[data-order-id]');
      var orderId = row.getAttribute('data-order-id');
      var newStatus = select.value;
      var badge = row.querySelector('[data-badge]');
      var invoiceBtn = row.querySelector('[data-invoice]');
      var prev = badge.textContent;

      select.disabled = true;
      fetchJSON(
        BASE + '/api/online-orders',
        {
          method: 'POST',
          headers: { 'Content-Type':'application/json' },
          body: JSON.stringify({ action: 'updateStatus', orderId: orderId, status: newStatus })
        }
      ).then(function(res){
        if (res.status === 'ok') {
          badge.textContent = newStatus;
          badge.classList.remove('pending','paid','cancelled');
          badge.classList.add(badgeClass(newStatus));
          if (invoiceBtn) invoiceBtn.style.display = (newStatus === 'PAID') ? 'inline-block' : 'none';
        } else {
          alert('Update failed: ' + (res.message || 'Unknown error'));
          select.value = prev;
        }
      }).catch(function(err){
        console.error(err);
        alert('Server error while updating status.');
        select.value = prev;
      }).finally(function(){ select.disabled = false; });
    });
  </script>
</body>
</html>

