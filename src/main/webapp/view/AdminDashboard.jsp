<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, com.pahanaedu.model.Book, com.pahanaedu.model.Accessory, com.pahanaedu.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>POS | Pahana Edu Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
  <style>
    /* Your style from earlier (copy-paste or use main.css) */
    body { background: var(--light); }
    .pos-wrapper { display: grid; grid-template-columns: 1fr 380px; gap: 36px; margin-top: 35px; align-items: flex-start; }
    @media(max-width:900px){ .pos-wrapper { display:block; } .cart-panel { margin-top:32px; } }
    .cart-panel { background: var(--white); box-shadow:0 2px 6px rgba(0,0,0,.07); border-radius:12px; padding:18px 15px; min-height:320px; position:sticky; top:100px; }
    .cart-table td input { width: 44px; }
    .searchbox { margin-bottom: 1em; }
    .cart-header { font-size: 21px; color:var(--purple); margin-bottom:12px; }
    .cart-total-row { font-weight:bold; font-size: 18px; margin-top: 10px; padding-top: 10px; border-top: 2px solid var(--purple); }
    .main-content { }
    .table { width:100%; margin-bottom:18px; }
    .cart-panel .searchbox { margin-bottom: 14px; padding-bottom: 8px; border-bottom: 1px solid #eee; }
    .cart-panel #customerSearch, .cart-panel #customerSelect { width: 100%; display: block; margin-bottom:8px; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    .cart-panel #btnNewCustomer { width:100%; margin-top:7px; }
    .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.4); z-index: 1000; justify-content: center; align-items: center; }
    .modal-overlay.show { display: flex; }
    .modal-content { background: white; padding: 28px; border-radius: 12px; width: 420px; max-width: 90%; box-shadow: 0 4px 20px rgba(0,0,0,0.15); }
    .modal-header { font-size: 20px; color: var(--purple); margin-bottom: 20px; font-weight: bold; }
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; color: #555; font-size: 14px; }
    .form-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; }
    .form-group input:focus { outline: none; border-color: var(--purple); }
    .modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
    .btn-cancel { background: #f0f0f0; color: #333; border: none; }
    .btn-cancel:hover { background: #e0e0e0; }
    .stock-low { color: #e74c3c; font-weight: bold; }
    .stock-ok { color: #27ae60; }
    .item-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-size: 14px; }
  </style>
</head>
<body>
  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions">
      <button id="toggleSidebar" aria-label="menu">&#9776;</button>
    </div>
    <div class="brand">Pahana Edu <span>Admin POS</span></div>
    <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar">
  </header>

  <!-- SLIDE SIDEBAR (LEFT) -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <a href="${pageContext.request.contextPath}/AdminPOSController">Dashboard</a>
      <a href="#" class="active">POS</a>
      <a href="${pageContext.request.contextPath}/items">Items</a>
      <a href="${pageContext.request.contextPath}/Users.jsp">Users</a>
      <a href="${pageContext.request.contextPath}/Staff.jsp">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout">Logout</a>
    </nav>
  </aside>

  <!-- OVERLAY -->
  <div id="overlay" class="overlay"></div>

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
            <tr>
              <th>Title</th>
              <th>Author</th>
              <th>Stock</th>
              <th>Price (Rs.)</th>
              <th>Action</th>
            </tr>
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
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>Stock</th>
              <th>Price (Rs.)</th>
              <th>Action</th>
            </tr>
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
  <!-- Customer select/search/add inside shopping cart -->
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
      <tr>
        <th>Item</th>
        <th>Qty</th>
        <th>Price</th>
        <th>Total</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <tr><td colspan="5" style="text-align:center;color:#999;">Cart is empty</td></tr>
    </tbody>
  </table>
  
  <div class="cart-total-row">
    <span>Total: Rs.</span> 
    <span id="cartTotal">0.00</span>
  </div>
  
  <button class="btn primary" id="btnPlaceOrder" style="width:100%;margin-top:18px;">
    💳 Place Order
  </button>
  <button class="btn" id="btnClearCart" style="width:100%;margin-top:10px;">
    🗑️ Clear Cart
  </button>
</div>

<!-- Enhanced Add Customer Modal -->
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
      <p style="font-size:12px;color:#666;margin-top:10px;">
        Note: Customer code will be auto-generated. Default password will be the customer code.
      </p>
      <div class="modal-footer">
        <button type="button" id="closeCustomerModal" class="btn btn-cancel">Cancel</button>
        <button type="submit" class="btn primary">Add Customer</button>
      </div>
    </form>
  </div>
</div>

<script>
  // (Paste all your JS for cart, modal, and customer search from before here!)
  // -- Sidebar toggling
  const sidebar   = document.getElementById('sidebar');
  const overlay   = document.getElementById('overlay');
  const main      = document.getElementById('main');
  const toggleBtn = document.getElementById('toggleSidebar');
  
  function toggleSidebarFn(){
    if(!sidebar) return;
    sidebar.classList.toggle('open');
    overlay.classList.toggle('show');
    main.classList.toggle('shift');
  }
  toggleBtn && toggleBtn.addEventListener('click', toggleSidebarFn);
  overlay   && overlay.addEventListener('click', toggleSidebarFn);

  // -- Shopping cart logic
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
    
    cart.forEach((item, idx) => {
      let itemTotal = item.price * item.qty;
      total += itemTotal;
      tbody.innerHTML += `
        <tr>
          <td>${item.name}</td>
          <td>
            <input type="number" min="1" max="${item.stock}" value="${item.qty}" 
              data-idx="${idx}" class="cart-qty-input"/>
          </td>
          <td>${item.price.toFixed(2)}</td>
          <td>${itemTotal.toFixed(2)}</td>
          <td><button class="btn small remove-cart-btn" data-idx="${idx}">❌</button></td>
        </tr>
      `;
    });
    document.getElementById('cartTotal').textContent = total.toFixed(2);
  }
  
  // Add to cart
  document.addEventListener('click', function(e){
    if(e.target.classList.contains('add-cart-btn') && !e.target.disabled) {
      const btn = e.target;
      const type = btn.dataset.type;
      const id = btn.dataset.id;
      const stock = parseInt(btn.dataset.stock);
      
      if (stock <= 0) {
        alert('This item is out of stock!');
        return;
      }
      
      let found = cart.find(i => i.type==type && i.id==id);
      if(found) { 
        if(found.qty < found.stock) {
          found.qty++;
        } else {
          alert('Cannot add more than available stock!');
        }
      } else {
        cart.push({
          type: type,
          id: id,
          name: btn.dataset.name,
          price: parseFloat(btn.dataset.price),
          qty: 1,
          stock: stock
        });
      }
      updateCartTable();
    }
    
    if(e.target.classList.contains('remove-cart-btn')) {
      cart.splice(e.target.dataset.idx, 1); 
      updateCartTable();
    }
  });
  
  // Update quantity in cart
  document.addEventListener('input', function(e){
    if(e.target.classList.contains('cart-qty-input')) {
      let idx = e.target.dataset.idx;
      let val = parseInt(e.target.value);
      if(val < 1) val = 1;
      if(val > cart[idx].stock) val = cart[idx].stock;
      cart[idx].qty = val;
      updateCartTable();
    }
  });

  // Customer search
  document.getElementById('customerSearch').addEventListener('input', function(){
    const q = this.value.trim();
    fetch('CustomerSearchController?q=' + encodeURIComponent(q))
      .then(res => res.json())
      .then(users => {
        const sel = document.getElementById('customerSelect');
        sel.innerHTML = '<option value="">-- Select Customer --</option>';
        users.forEach(u => {
          sel.innerHTML += `<option value="${u.id}" data-code="${u.customerCode}">${u.username} [${u.customerCode}]</option>`;
        });
      })
      .catch(err => console.error('Search error:', err));
  });

  // Place order
  document.getElementById('btnPlaceOrder').addEventListener('click', function(){
    const customerSel = document.getElementById('customerSelect');
    
    if(cart.length == 0) { 
      alert('Cart is empty! Please add items to cart.'); 
      return; 
    }
    
    if(!customerSel.value) { 
      alert('Please select a customer or add a new one.'); 
      return; 
    }
    
    const customerId = customerSel.value;
    const customerCode = customerSel.selectedOptions[0].dataset.code;
    const customerName = customerSel.selectedOptions[0].textContent.split('[')[0].trim();
    const total = parseFloat(document.getElementById('cartTotal').textContent);
    
    // Confirm order
    if(!confirm(`Place order for ${customerName}?\nTotal: Rs. ${total.toFixed(2)}`)) {
      return;
    }
    
    fetch('OrderController', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
        customerId, 
        customerCode, 
        customerName, 
        total,
        cart: cart.map(i=>({
          type: i.type, 
          id: i.id, 
          name: i.name,
          qty: i.qty, 
          price: i.price
        }))
      })
    })
    .then(r => r.json())
    .then(res => {
      if(res.status === 'ok'){
        alert('Order placed successfully!\nInvoice Number: ' + res.invoiceNumber);
        cart = [];
        updateCartTable();
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        alert('Error placing order: ' + (res.message || 'Unknown error'));
      }
    })
    .catch(err => {
      console.error('Order error:', err);
      alert('Error placing order. Please try again.');
    });
  });

  // Clear cart
  document.getElementById('btnClearCart').addEventListener('click', function() {
    if(cart.length > 0) {
      if(confirm('Clear all items from cart?')) {
        cart = [];
        updateCartTable();
      }
    }
  });

  // --- Add Customer Modal ---
  const addCustomerBtn = document.getElementById('btnNewCustomer');
  const addCustomerModal = document.getElementById('addCustomerModal');
  const closeCustomerModal = document.getElementById('closeCustomerModal');

  addCustomerBtn.onclick = function() { 
    addCustomerModal.classList.add('show');
    document.getElementById('newCustomerForm').reset();
  }
  
  closeCustomerModal.onclick = function() { 
    addCustomerModal.classList.remove('show');
  }
  
  addCustomerModal.onclick = function(e) {
    if (e.target === addCustomerModal) {
      addCustomerModal.classList.remove('show');
    }
  };

  document.getElementById('newCustomerForm').onsubmit = function(e) {
    e.preventDefault();
    
    const name = document.getElementById('newCustomerName').value.trim();
    const address = document.getElementById('newCustomerAddress').value.trim();
    const telephone = document.getElementById('newCustomerTelephone').value.trim();
    const email = document.getElementById('newCustomerEmail').value.trim();
    
    if (!name) {
      alert('Customer name is required!');
      return;
    }

    fetch('AddCustomerController', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
        username: name,
        customerCode: '', // Will be auto-generated
        address: address,
        telephone: telephone,
        email: email
      })
    })
    .then(res => res.json())
    .then(cust => {
      if (cust && cust.id) {
        const sel = document.getElementById('customerSelect');
        const opt = document.createElement('option');
        opt.value = cust.id;
        opt.dataset.code = cust.customerCode;
        opt.textContent = `${cust.username} [${cust.customerCode}]`;
        sel.appendChild(opt);
        sel.value = cust.id;
        
        addCustomerModal.classList.remove('show');
        document.getElementById('newCustomerForm').reset();
        
        alert('Customer added successfully!\nCustomer Code: ' + cust.customerCode);
      } else {
        alert('Failed to add customer: ' + (cust.error || 'Unknown error'));
      }
    })
    .catch(err => {
      console.error('Add customer error:', err);
      alert('Failed to add customer. Please try again.');
    });
  };
  
  // Initialize cart display
  updateCartTable();
</script>
      <!-- Shopping Cart Section (as you had before) -->
      <!-- ... (the rest of your shopping cart, JS, and modal code as previously shown) ... -->
      <!-- Just copy the cart panel/modal/scripts from your previous working JSP here -->
     
    </div>
  </div>
</body>
</html>


