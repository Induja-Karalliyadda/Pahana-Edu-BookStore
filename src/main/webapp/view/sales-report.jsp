<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<title>Sales Report | Pahana Edu</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<style>
  :root{ --purple:#8e44ad; --light:#ecf0f1; --white:#fff; --ink:#2d2d2d;}
  body{ background:var(--light); color:var(--ink);}
  .topbar{ display:flex; align-items:center; justify-content:space-between; background:#fff; padding:10px 14px; box-shadow:0 2px 6px rgba(0,0,0,.07);}
  .brand{ font-weight:800; color:var(--purple);}
  .avatar{ width:34px; height:34px; border-radius:50%;}
  /* sidebar */
  .sidebar{ position:fixed; top:0; left:0; height:100vh; width:240px; background:#fff; border-right:1px solid #eee; padding:80px 14px 14px; transform:translateX(-100%); transition:.25s; z-index:999;}
  .sidebar.open{ transform:translateX(0); }
  .sidebar nav a{ display:block; padding:10px 12px; border-radius:8px; color:#333; text-decoration:none; margin-bottom:6px;}
  .sidebar nav a:hover, .sidebar nav a.active{ background:rgba(142,68,173,.12); color:var(--purple); }
  .menu-title{ color:#999; margin:0 0 8px 6px; font-size:12px; text-transform:uppercase; letter-spacing:.06em;}
  .overlay{ display:none; position:fixed; inset:0; background:rgba(0,0,0,.25); z-index:998;}
  .overlay.show{ display:block; }
  .main{ padding:100px 24px 32px; max-width:1100px; margin:0 auto; transition:.25s;}
  .main.shift{ transform: translateX(220px); }

  /* cards */
  .kpi{ display:grid; grid-template-columns: repeat(4,1fr); gap:14px; margin:12px 0 20px;}
  @media(max-width:980px){ .kpi{ grid-template-columns: repeat(2,1fr);} }
  @media(max-width:560px){ .kpi{ grid-template-columns: 1fr;} }
  .card{ background:#fff; border-radius:12px; padding:16px; box-shadow:0 2px 6px rgba(0,0,0,.07); }
  .card h4{ margin:0 0 6px; color:#777; font-weight:600; }
  .card .big{ font-size:24px; font-weight:800; color:var(--purple); }

  .toolbar{ display:flex; gap:10px; align-items:center; flex-wrap:wrap; margin:8px 0 16px;}
  .toolbar input[type="date"]{ padding:8px 10px; border:1px solid #ddd; border-radius:8px;}
  .btn{ background:var(--purple); color:#fff; border:none; padding:8px 12px; border-radius:10px; cursor:pointer;}
  .btn.light{ background:#f0e6f7; color:#5e2b84; }

  table{ width:100%; border-collapse:collapse; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 2px 6px rgba(0,0,0,.07);}
  th,td{ padding:10px 12px; border-bottom:1px solid #eee; }
  th{ text-align:left; background:#fafafa; color:#666; }

  /* topbar button */
  #toggleSidebar{ background:transparent; border:none; font-size:22px; cursor:pointer; }
</style>
</head>
<body>
<header class="topbar">
  <div class="top-actions">
    <button id="toggleSidebar" aria-label="menu">&#9776;</button>
  </div>
  <div class="brand">Pahana Edu <span> Reports</span></div>
  <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar">
</header>

<aside id="sidebar" class="sidebar">
  <nav>
    <h4 class="menu-title">Menu</h4>
    <c:url var="dashboardUrl" value="/AdminPOSController"/>
    <c:url var="itemsBookUrl" value="/items"><c:param name="mainCategory" value="book"/></c:url>
    <c:url var="ordersUrl" value="/online-orders"/>
    <c:url var="salesReportUrl" value="/sales-report"/>
    <c:url var="logoutUrl" value="/logout"/>

    <a href="${dashboardUrl}">Dashboard</a>
    <a href="${itemsBookUrl}">Items</a>
    <a href="${ordersUrl}">Orders</a>
    <a href="${salesReportUrl}" class="active">Sales Report</a>
    <a href="${logoutUrl}" class="logout">Logout</a>
  </nav>
</aside>

<div class="overlay" id="overlay"></div>

<div class="main" id="main">
  <h1 style="color:var(--purple);margin:0 0 6px;">Daily Sales Report</h1>
  <div class="toolbar">
    <label for="day">Select day:</label>
    <input type="date" id="day"/>
    <button class="btn" id="btnLoad">Load</button>
    <button class="btn light" id="btnToday">Today</button>
    <button class="btn light" id="btnYesterday">Yesterday</button>
    <span id="status" style="margin-left:auto;color:#777;"></span>
  </div>

  <div class="kpi">
    <div class="card">
      <h4>Orders</h4>
      <div class="big" id="kpiOrders">0</div>
    </div>
    <div class="card">
      <h4>Units Sold</h4>
      <div class="big" id="kpiUnits">0</div>
    </div>
    <div class="card">
      <h4>Revenue (Rs.)</h4>
      <div class="big" id="kpiRevenue">0.00</div>
    </div>
    <div class="card">
      <h4>Profit (Rs.)</h4>
      <div class="big" id="kpiProfit">0.00</div>
    </div>
  </div>

  <h2 style="color:var(--purple);margin:6px 0 10px;">Items Sold</h2>
  <table id="tblItems">
    <thead>
      <tr>
        <th>Item</th>
        <th>Type</th>
        <th>Qty</th>
        <th>Revenue (Rs.)</th>
        <th>Profit (Rs.)</th>
      </tr>
    </thead>
    <tbody>
      <tr><td colspan="5" style="text-align:center;color:#999;">No data</td></tr>
    </tbody>
  </table>
</div>

<script>
  const BASE = '<c:out value="${pageContext.request.contextPath}"/>';
  // Sidebar toggle same as POS
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('overlay');
  const main = document.getElementById('main');
  const toggleBtn = document.getElementById('toggleSidebar');
  function toggleSidebarFn(){
    sidebar.classList.toggle('open');
    overlay.classList.toggle('show');
    main.classList.toggle('shift');
  }
  toggleBtn.addEventListener('click', toggleSidebarFn);
  overlay.addEventListener('click', toggleSidebarFn);

  function fmt(n){ return Number(n || 0).toLocaleString(undefined,{minimumFractionDigits:2, maximumFractionDigits:2}); }

  function render(data){
    const s = data.summary || {};
    document.getElementById('kpiOrders').textContent  = s.orders || 0;
    document.getElementById('kpiUnits').textContent   = s.units || 0;
    document.getElementById('kpiRevenue').textContent = fmt(s.revenue);
    document.getElementById('kpiProfit').textContent  = fmt(s.profit);
    document.getElementById('status').textContent     = 'Day: ' + (s.day || '');

    const tbody = document.querySelector('#tblItems tbody');
    tbody.innerHTML = '';
    (data.items || []).forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML =
        '<td>'+ r.name +'</td>'+
        '<td>'+ r.type +'</td>'+
        '<td>'+ r.qty +'</td>'+
        '<td>'+ fmt(r.revenue) +'</td>'+
        '<td>'+ fmt(r.profit) +'</td>';
      tbody.appendChild(tr);
    });
    if ((data.items || []).length === 0){
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#999;">No data</td></tr>';
    }
  }

  function loadDay(d){
    if(!d){ return; }
    document.getElementById('status').textContent = 'Loading...';
    fetch(BASE + '/sales-report?format=json&day=' + d + '&_=' + Date.now(), {cache:'no-store'})
      .then(r => r.json())
      .then(render)
      .catch(err => { console.error(err); alert('Failed to load report'); })
      .finally(() => { document.getElementById('status').textContent = 'Day: ' + d; });
  }

  const dayInput = document.getElementById('day');
  function todayStr(){ return new Date().toISOString().slice(0,10); }
  function yesterdayStr(){ const t=new Date(); t.setDate(t.getDate()-1); return t.toISOString().slice(0,10); }

  document.getElementById('btnLoad').addEventListener('click', ()=> loadDay(dayInput.value));
  document.getElementById('btnToday').addEventListener('click', ()=> { dayInput.value = todayStr(); loadDay(dayInput.value); });
  document.getElementById('btnYesterday').addEventListener('click', ()=> { dayInput.value = yesterdayStr(); loadDay(dayInput.value); });

  // init
  dayInput.value = todayStr();
  loadDay(dayInput.value);
</script>
</body>
</html>
