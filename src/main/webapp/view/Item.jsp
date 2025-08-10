<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Item Management - Pahana Edu</title>
  <c:url var="adminCss" value="/css/admin.css"/>
  <link rel="stylesheet" href="${adminCss}" />
  <style>
    body { background: #ecf0f1; margin: 0; font-family: 'Segoe UI', sans-serif; }
    .container { max-width: 1100px; margin: 32px auto 0; padding:0 8px; }
    .btn { padding: 7px 20px; border-radius: 7px; border: 1.5px solid #8e44ad; background: #fff; color: #8e44ad; font-weight: 600; cursor: pointer; margin-right: 7px; text-decoration: none; display: inline-block; }
    .btn.active, .btn:focus { background: #8e44ad; color: #fff; }
    .form-card { max-width: 640px; margin: 16px auto 30px auto; padding: 2.2rem 2rem; background: #fff; border-radius: 1.2rem; box-shadow: 0 6px 32px rgba(44,62,80,0.10); display: flex; flex-direction: column; position: relative; }
    .form-card h1 { margin-bottom: 1.3rem; font-size: 1.6rem; color: #6c3483; text-align: center; }
    .main-table, .main-table th, .main-table td { border: 1px solid #ddd; border-collapse: collapse; }
    .main-table { width: 100%; margin: 24px auto 30px auto; background: #fff; border-radius: 10px; box-shadow: 0 2px 16px rgba(44,62,80,0.07); font-size: 1rem; }
    .main-table th, .main-table td { padding: 0.6rem 0.5rem; text-align: left; vertical-align: middle; }
    .main-table th { background: #8e44ad; color: #fff; font-weight: 600; }
    .main-table tr:nth-child(even) { background: #f9f7fa; }
    .main-table img { height: 48px; width: 48px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; }
    .actions .btn { margin: 0 2px; font-size: 0.85em; padding: 5px 12px; }
    .message, .error { max-width: 640px; margin: 20px auto; padding: 1rem; border-radius: 6px; text-align: center; }
    .message { background: #dff0d8; color: #3c763d; }
    .error   { background: #f2dede; color: #a94442; }
    .hidden { display: none; }
    .search-box { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; background: #fff; padding: 12px 18px; border-radius: 8px; margin: 10px 0 20px 0; box-shadow: 0 1px 8px rgba(0,0,0,0.08); }
    .search-box input, .search-box select { padding: 6px 12px; border-radius: 6px; border: 1px solid #bbb; font-size: 0.9rem; flex: 1; min-width: 150px; }
    .top-controls { display: flex; flex-wrap: wrap; justify-content: space-between; gap: 10px; margin-bottom: 8px; }
    .category-switch { display: flex; gap: 4px; }
    .form-group { margin-bottom: 1rem; display: flex; flex-direction: column; }
    .form-group label { margin-bottom: 0.3rem; font-weight: 600; }
    .form-group input,
    .form-group select,
    .form-group textarea { padding: 0.5rem; font-size: 0.95rem; border: 1px solid #dcdcdc; border-radius: 6px; outline: none; background: #faf9fa; transition: border .2s; }
    .form-group input:focus,
    .form-group select:focus,
    .form-group textarea:focus { border: 1.5px solid #8e44ad; }
    .preview-img { height: 80px; width: 80px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; margin-top:6px; }
    .sidebar.open { transform: translateX(0); }
    .overlay.active { display: block; }
    .small-note { font-size: 0.7rem; margin-bottom: 4px; color: #555; }
    .low-stock { color: #d9534f; font-weight: 700; }
    .below-min-badge { background: #f8d7da; color: #842029; padding: 2px 6px; border-radius: 4px; font-size: 0.6rem; margin-left: 4px; display: inline-block; }
    .filter-note { margin: 10px 0; padding: 10px; background: #fff3cd; border: 1px solid #ffeeba; border-radius: 6px; color: #856404; font-size: 0.9rem; }
    @media (max-width: 900px) { .main-table, .main-table th, .main-table td { font-size: 0.9rem; } }
    @media (max-width: 600px) { .main-table { font-size: 0.85rem; } }
  </style>
</head>
<body>

  <c:set var="showAddForm" value="${param.showForm == '1'}"/>
  <c:if test="${empty mainCategory or (mainCategory != 'book' and mainCategory != 'accessory')}">
    <c:set var="mainCategory" value="book"/>
  </c:if>

  <c:choose>
    <c:when test="${mainCategory == 'book'}">
      <c:set var="bookActive" value="btn active"/>
      <c:set var="accessoryActive" value="btn"/>
      <c:set var="searchPlaceholder" value="Book Name"/>
    </c:when>
    <c:otherwise>
      <c:set var="bookActive" value="btn"/>
      <c:set var="accessoryActive" value="btn active"/>
      <c:set var="searchPlaceholder" value="Accessory Name"/>
    </c:otherwise>
  </c:choose>

  <c:set var="isEdit" value="${not empty editBook or not empty editAccessory}"/>

  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions"><button id="toggleSidebar" aria-label="Toggle menu">&#9776;</button></div>
    <div class="brand">Pahana Edu <span>Admin</span></div>
    <c:url var="avatarUrl" value="/img/avatar.png"/>
    <img src="${avatarUrl}" alt="Admin" class="avatar" />
  </header>

  <!-- SIDEBAR -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <c:url var="dashboardUrl" value="/view/AdminDashboard.jsp"/>
      <c:url var="itemsBookUrl" value="/items"><c:param name="mainCategory" value="book"/></c:url>
      <c:url var="usersUrl" value="/users"/>
      <c:url var="staffUrl" value="/staff"/>
      <c:url var="logoutUrl" value="/LogoutServlet"/>
      <a href="${dashboardUrl}">Dashboard</a>
      <a href="#">Orders</a>
      <a href="${itemsBookUrl}" class="active">Item</a>
      <a href="${usersUrl}">Users</a>
      <a href="${staffUrl}">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${logoutUrl}" class="logout">Logout</a>
    </nav>
  </aside>
  <div id="overlay" class="overlay" style="display:none;"></div>

  <main class="main container" id="main">
    <c:if test="${param.success == '1'}">
      <div class="message">✅ Item saved successfully.</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
      <div class="message">✅ Item deleted successfully.</div>
    </c:if>
    <c:if test="${not empty param.error}">
      <div class="error">❌ Error: ${param.error}</div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="error">❌ Error: ${error}</div>
    </c:if>

    <div class="top-controls">
      <div class="category-switch">
        <form action="<c:url value='/items'/>" method="get" style="margin:0;">
          <input type="hidden" name="mainCategory" value="book"/>
          <button type="submit" class="${bookActive}">Books</button>
        </form>
        <form action="<c:url value='/items'/>" method="get" style="margin:0;">
          <input type="hidden" name="mainCategory" value="accessory"/>
          <button type="submit" class="${accessoryActive}">Accessories</button>
        </form>
      </div>
      <div style="display:flex; gap:6px;">
        <c:url var="addBookUrl" value="/items">
          <c:param name="mainCategory" value="book"/>
          <c:param name="showForm" value="1"/>
        </c:url>
        <c:url var="addAccUrl" value="/items">
          <c:param name="mainCategory" value="accessory"/>
          <c:param name="showForm" value="1"/>
        </c:url>
        <a href="${addBookUrl}" class="btn">+ Add Book</a>
        <a href="${addAccUrl}" class="btn">+ Add Accessory</a>
      </div>
    </div>

    <!-- Add / Edit Item Form -->
    <div class="form-card ${ (showAddForm or isEdit) ? '' : 'hidden' }" id="addItemFormCard">
      <h1 id="formTitle">
        <c:choose>
          <c:when test="${isEdit}">
            <c:choose>
              <c:when test="${mainCategory == 'book'}">Edit Book</c:when>
              <c:otherwise>Edit Accessory</c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <c:choose>
              <c:when test="${mainCategory == 'book'}">Add Book</c:when>
              <c:otherwise>Add Accessory</c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </h1>
      <form id="itemForm" action="<c:url value='/items'/>" method="post" enctype="multipart/form-data">
        <input type="hidden" name="mainCategory" value="${mainCategory}" />
        <c:if test="${empty editBook and empty editAccessory}">
          <input type="hidden" name="action" value="create" />
        </c:if>
        <c:if test="${not empty editBook}">
          <input type="hidden" name="action" value="update" />
          <input type="hidden" name="bookId" value="${editBook.bookId}" />
        </c:if>
        <c:if test="${not empty editAccessory}">
          <input type="hidden" name="action" value="update" />
          <input type="hidden" name="accessoryId" value="${editAccessory.accessoryId}" />
        </c:if>

        <!-- Book Fields -->
        <div id="bookFields" class="${mainCategory != 'book' ? 'hidden' : ''}">
          <div class="form-group">
            <label for="title">Title</label>
            <input type="text" name="title" id="title" required value="${not empty editBook ? editBook.title : ''}" />
          </div>
          <div class="form-group">
            <label for="author">Author</label>
            <input type="text" name="author" id="author" required value="${not empty editBook ? editBook.author : ''}" />
          </div>
          <div class="form-group">
            <label for="bookSub">Category</label>
            <select name="bookSub" id="bookSub" required>
              <option value="">Select Category</option>
              <c:forEach var="cat" items="${bookCategories}">
                <option value="${cat}" <c:if test="${not empty editBook and editBook.category == cat}">selected</c:if>>${cat}</option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="descriptionBook">Description</label>
            <textarea name="descriptionBook" id="descriptionBook" rows="3">${not empty editBook ? editBook.description : ''}</textarea>
          </div>
          <div class="form-group">
            <label for="supplierBook">Supplier</label>
            <input type="text" name="supplierBook" id="supplierBook" value="${not empty editBook ? editBook.supplier : ''}" />
          </div>
          <div class="form-group">
            <label for="costPriceBook">Cost Price</label>
            <input type="number" step="0.01" name="costPriceBook" id="costPriceBook" value="${not empty editBook ? editBook.costPrice : '0'}" />
          </div>
          <div class="form-group">
            <label for="sellingPriceBook">Selling Price</label>
            <input type="number" step="0.01" name="sellingPriceBook" id="sellingPriceBook" value="${not empty editBook ? editBook.sellingPrice : '0'}" />
          </div>
          <div class="form-group">
            <label for="stockBook">Stock</label>
            <input type="number" name="stockBook" id="stockBook" value="${not empty editBook ? editBook.stock : '0'}" />
          </div>
          <div class="form-group">
            <label for="minStockBook">Min Stock</label>
            <input type="number" name="minStockBook" id="minStockBook" value="${not empty editBook ? editBook.minStock : '0'}" />
          </div>
          <div class="form-group">
            <label for="imgBook">Image</label>
            <input type="file" name="imgBook" id="imgBook" accept="image/*" />
            <div id="newBookPreviewContainer" style="display: none;">
              <img id="newBookPreview" class="preview-img" src="#" alt="New Book Preview" />
            </div>
            <c:if test="${not empty editBook and not empty editBook.imageUrl}">
              <div class="small-note">Current Image:</div>
              <img class="preview-img" src="<c:url value='/${editBook.imageUrl}'/>" alt="Existing Book Image" />
            </c:if>
          </div>
        </div>

        <!-- Accessory Fields -->
        <div id="accessoryFields" class="${mainCategory != 'accessory' ? 'hidden' : ''}">
          <div class="form-group">
            <label for="itemName">Name</label>
            <input type="text" name="itemName" id="itemName" required value="${not empty editAccessory ? editAccessory.name : ''}" />
          </div>
          <div class="form-group">
            <label for="accSub">Category</label>
            <select name="accSub" id="accSub" required>
              <option value="">Select Category</option>
              <c:forEach var="cat" items="${accessoryCategories}">
                <option value="${cat}" <c:if test="${not empty editAccessory and editAccessory.category == cat}">selected</c:if>>${cat}</option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="descriptionAcc">Description</label>
            <textarea name="descriptionAcc" id="descriptionAcc" rows="3">${not empty editAccessory ? editAccessory.description : ''}</textarea>
          </div>
          <div class="form-group">
            <label for="supplierAcc">Supplier</label>
            <input type="text" name="supplierAcc" id="supplierAcc" value="${not empty editAccessory ? editAccessory.supplier : ''}" />
          </div>
          <div class="form-group">
            <label for="costPriceAcc">Cost Price</label>
            <input type="number" step="0.01" name="costPriceAcc" id="costPriceAcc" value="${not empty editAccessory ? editAccessory.costPrice : '0'}" />
          </div>
          <div class="form-group">
            <label for="sellingPriceAcc">Selling Price</label>
            <input type="number" step="0.01" name="sellingPriceAcc" id="sellingPriceAcc" value="${not empty editAccessory ? editAccessory.sellingPrice : '0'}" />
          </div>
          <div class="form-group">
            <label for="stockAcc">Stock</label>
            <input type="number" name="stockAcc" id="stockAcc" value="${not empty editAccessory ? editAccessory.stock : '0'}" />
          </div>
          <div class="form-group">
            <label for="minStockAcc">Min Stock</label>
            <input type="number" name="minStockAcc" id="minStockAcc" value="${not empty editAccessory ? editAccessory.minStock : '0'}" />
          </div>
          <div class="form-group">
            <label for="imgAcc">Image</label>
            <input type="file" name="imgAcc" id="imgAcc" accept="image/*" />
            <div id="newAccPreviewContainer" style="display: none;">
              <img id="newAccPreview" class="preview-img" src="#" alt="New Accessory Preview" />
            </div>
            <c:if test="${not empty editAccessory and not empty editAccessory.imageUrl}">
              <div class="small-note">Current Image:</div>
              <img class="preview-img" src="<c:url value='/${editAccessory.imageUrl}'/>" alt="Existing Accessory Image" />
            </c:if>
          </div>
        </div>

        <button type="submit" id="submitBtn" class="btn" style="width:100%; margin-top:10px;">
          <c:choose>
            <c:when test="${isEdit}">Update Item</c:when>
            <c:otherwise>Save Item</c:otherwise>
          </c:choose>
        </button>
      </form>
    </div>

    <!-- Search bar -->
    <form class="search-box" action="<c:url value='/items'/>" method="get">
      <input type="hidden" name="mainCategory" value="${mainCategory}" />
      <input type="text" name="searchName" placeholder="${searchPlaceholder}" value="${param.searchName}" />
      <select name="searchCategory">
        <option value="">All Categories</option>
        <c:choose>
          <c:when test="${mainCategory == 'book'}">
            <option value="Educational" <c:if test="${param.searchCategory == 'Educational'}">selected</c:if>>Educational</option>
            <option value="Children's Books" <c:if test="${param.searchCategory == \"Children's Books\"}">selected</c:if>>Children's Books</option>
            <option value="Fiction" <c:if test="${param.searchCategory == 'Fiction'}">selected</c:if>>Fiction</option>
            <option value="Non-Fiction" <c:if test="${param.searchCategory == 'Non-Fiction'}">selected</c:if>>Non-Fiction</option>
            <option value="Comics & Graphic Novels" <c:if test="${param.searchCategory == 'Comics & Graphic Novels'}">selected</c:if>>Comics & Graphic Novels</option>
            <option value="Technology & Science" <c:if test="${param.searchCategory == 'Technology & Science'}">selected</c:if>>Technology & Science</option>
            <option value="Language Learning" <c:if test="${param.searchCategory == 'Language Learning'}">selected</c:if>>Language Learning</option>
            <option value="Art & Design" <c:if test="${param.searchCategory == 'Art & Design'}">selected</c:if>>Art & Design</option>
            <option value="Cookbooks & Food" <c:if test="${param.searchCategory == 'Cookbooks & Food'}">selected</c:if>>Cookbooks & Food</option>
            <option value="Poetry" <c:if test="${param.searchCategory == 'Poetry'}">selected</c:if>>Poetry</option>
            <option value="Law" <c:if test="${param.searchCategory == 'Law'}">selected</c:if>>Law</option>
            <option value="Sports & Outdoors" <c:if test="${param.searchCategory == 'Sports & Outdoors'}">selected</c:if>>Sports & Outdoors</option>
          </c:when>
          <c:when test="${mainCategory == 'accessory'}">
            <option value="Writing Instruments" <c:if test="${param.searchCategory == 'Writing Instruments'}">selected</c:if>>Writing Instruments</option>
            <option value="Erasers & Correction" <c:if test="${param.searchCategory == 'Erasers & Correction'}">selected</c:if>>Erasers & Correction</option>
            <option value="Paper Products" <c:if test="${param.searchCategory == 'Paper Products'}">selected</c:if>>Paper Products</option>
            <option value="Files & Folders" <c:if test="${param.searchCategory == 'Files & Folders'}">selected</c:if>>Files & Folders</option>
          </c:when>
        </c:choose>
      </select>
      <button class="btn" type="submit">Search</button>
      <c:choose>
        <c:when test="${param.lowStock == '1'}">
          <c:url var="clearLowUrl" value="/items">
            <c:param name="mainCategory" value="${mainCategory}" />
            <c:if test="${not empty param.searchName}"><c:param name="searchName" value="${param.searchName}" /></c:if>
            <c:if test="${not empty param.searchCategory}"><c:param name="searchCategory" value="${param.searchCategory}" /></c:if>
          </c:url>
          <a href="${clearLowUrl}" class="btn" style="background:#f5c6cb; border-color:#d9534f;">Clear Low Stock</a>
        </c:when>
        <c:otherwise>
          <button class="btn" type="submit" name="lowStock" value="1" style="background:#fff3cd; border-color:#e2b822;">Low Stock</button>
        </c:otherwise>
      </c:choose>
    </form>

    <c:if test="${param.lowStock == '1'}">
      <div class="filter-note">
        Showing <strong>low-stock</strong> items only (stock less than or equal to minimum stock). Remove the filter to see all items.
      </div>
    </c:if>

    <!-- Tables -->
    <c:choose>
      <c:when test="${mainCategory == 'accessory'}">
        <table class="main-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Category</th>
              <th>Description</th>
              <th>Supplier</th>
              <th>Cost Price</th>
              <th>Selling Price</th>
              <th>Stock</th>
              <th>Min Stock</th>
              <th>Image</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="a" items="${accessories}" varStatus="loop">
              <c:if test="${param.lowStock != '1' or (a.stock <= a.minStock)}">
                <tr>
                  <td>${loop.index + 1}</td>
                  <td>${a.name}</td>
                  <td>${a.category}</td>
                  <td style="max-width:180px;word-break:break-all;">${a.description}</td>
                  <td>${a.supplier}</td>
                  <td>${a.costPrice}</td>
                  <td>${a.sellingPrice}</td>
                  <td>
                    <c:choose>
                      <c:when test="${a.stock lt a.minStock}">
                        <span class="low-stock">${a.stock}</span>
                        <span class="below-min-badge">Below Min</span>
                      </c:when>
                      <c:when test="${a.stock eq a.minStock}">
                        <span class="low-stock">${a.stock}</span>
                      </c:when>
                      <c:otherwise>
                        ${a.stock}
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>${a.minStock}</td>
                  <td>
                    <c:if test="${not empty a.imageUrl}">
                      <img src="<c:url value='/${a.imageUrl}'/>" alt="Accessory Image" />
                    </c:if>
                  </td>
                  <td class="actions">
                    <c:url var="editAccUrl" value="/items">
                      <c:param name="action" value="edit"/>
                      <c:param name="type" value="accessory"/>
                      <c:param name="id" value="${a.accessoryId}"/>
                      <c:param name="mainCategory" value="accessory"/>
                    </c:url>
                    <c:url var="deleteAccUrl" value="/items">
                      <c:param name="action" value="delete"/>
                      <c:param name="type" value="accessory"/>
                      <c:param name="id" value="${a.accessoryId}"/>
                      <c:param name="mainCategory" value="accessory"/>
                    </c:url>
                    <a class="btn" href="${editAccUrl}">Edit</a>
                    <a class="btn" href="${deleteAccUrl}" onclick="return confirm('Delete accessory?');">Delete</a>
                  </td>
                </tr>
              </c:if>
            </c:forEach>
          </tbody>
        </table>
      </c:when>
      <c:otherwise>
        <table class="main-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Title</th>
              <th>Author</th>
              <th>Category</th>
              <th>Description</th>
              <th>Supplier</th>
              <th>Cost Price</th>
              <th>Selling Price</th>
              <th>Stock</th>
              <th>Min Stock</th>
              <th>Image</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="b" items="${books}" varStatus="loop">
              <c:if test="${param.lowStock != '1' or (b.stock <= b.minStock)}">
                <tr>
                  <td>${loop.index + 1}</td>
                  <td>${b.title}</td>
                  <td>${b.author}</td>
                  <td>${b.category}</td>
                  <td style="max-width:180px;word-break:break-all;">${b.description}</td>
                  <td>${b.supplier}</td>
                  <td>${b.costPrice}</td>
                  <td>${b.sellingPrice}</td>
                  <td>
                    <c:choose>
                      <c:when test="${b.stock lt b.minStock}">
                        <span class="low-stock">${b.stock}</span>
                        <span class="below-min-badge">Below Min</span>
                      </c:when>
                      <c:when test="${b.stock eq b.minStock}">
                        <span class="low-stock">${b.stock}</span>
                      </c:when>
                      <c:otherwise>
                        ${b.stock}
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>${b.minStock}</td>
                  <td>
                    <c:if test="${not empty b.imageUrl}">
                      <img src="<c:url value='/${b.imageUrl}'/>" alt="Book Image" />
                    </c:if>
                  </td>
                  <td class="actions">
                    <c:url var="editBookUrl" value="/items">
                      <c:param name="action" value="edit"/>
                      <c:param name="type" value="book"/>
                      <c:param name="id" value="${b.bookId}"/>
                      <c:param name="mainCategory" value="book"/>
                    </c:url>
                    <c:url var="deleteBookUrl" value="/items">
                      <c:param name="action" value="delete"/>
                      <c:param name="type" value="book"/>
                      <c:param name="id" value="${b.bookId}"/>
                      <c:param name="mainCategory" value="book"/>
                    </c:url>
                    <a class="btn" href="${editBookUrl}">Edit</a>
                    <a class="btn" href="${deleteBookUrl}" onclick="return confirm('Delete book?');">Delete</a>
                  </td>
                </tr>
              </c:if>
            </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </main>

  <script>
    const mainCategory = "${mainCategory}";
    const isEdit = ${isEdit};

    // Sidebar toggle
    document.getElementById('toggleSidebar')?.addEventListener('click', ()=>{
      document.getElementById('sidebar')?.classList.toggle('open');
      const ov = document.getElementById('overlay');
      ov?.classList.toggle('active');
      if (ov) ov.style.display = ov.classList.contains('active') ? 'block' : 'none';
    });
    document.getElementById('overlay')?.addEventListener('click', ()=>{
      document.getElementById('sidebar')?.classList.remove('open');
      const ov = document.getElementById('overlay');
      if (ov) { ov.classList.remove('active'); ov.style.display='none'; }
    });

    function adjustFormControls() {
      if (mainCategory === 'book') {
        document.querySelectorAll('#accessoryFields input, #accessoryFields select, #accessoryFields textarea').forEach(el => {
          el.disabled = true;
        });
        document.querySelectorAll('#bookFields input, #bookFields select, #bookFields textarea').forEach(el => {
          el.disabled = false;
        });
      } else {
        document.querySelectorAll('#bookFields input, #bookFields select, #bookFields textarea').forEach(el => {
          el.disabled = true;
        });
        document.querySelectorAll('#accessoryFields input, #accessoryFields select, #accessoryFields textarea').forEach(el => {
          el.disabled = false;
        });
      }
    }
    adjustFormControls();

    function previewFile(inputElem, previewImgId, containerId) {
      if (!inputElem) return;
      inputElem.addEventListener('change', () => {
        const file = inputElem.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = e => {
          document.getElementById(previewImgId).src = e.target.result;
          document.getElementById(containerId).style.display = 'block';
        };
        reader.readAsDataURL(file);
      });
    }
    previewFile(document.getElementById('imgBookInput') || document.getElementById('imgBook'), 'newBookPreview', 'newBookPreviewContainer');
    previewFile(document.getElementById('imgAccInput') || document.getElementById('imgAcc'), 'newAccPreview', 'newAccPreviewContainer');
  </script>
</body>
</html>


  