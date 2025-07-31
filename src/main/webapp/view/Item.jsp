<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Item Management - Pahana Edu</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
  <style>
    body { background: #ecf0f1; margin: 0; font-family: 'Segoe UI', sans-serif; }
    .container { max-width: 1100px; margin: 32px auto 0; padding:0 8px; }
    .btn { padding: 7px 20px; border-radius: 7px; border: 1.5px solid #8e44ad; background: #fff; color: #8e44ad; font-weight: 600; cursor: pointer; margin-right: 7px; text-decoration: none; display: inline-block; }
    .btn.active, .btn:focus { background: #8e44ad; color: #fff; }
    .form-card { max-width: 540px; margin: 16px auto 30px auto; padding: 2.2rem 2rem; background: #fff; border-radius: 1.2rem; box-shadow: 0 6px 32px rgba(44,62,80,0.10); display: flex; flex-direction: column; position: relative; }
    .form-card h1 { margin-bottom: 1.3rem; font-size: 1.6rem; color: #6c3483; text-align: center; }
    .main-table, .main-table th, .main-table td { border: 1px solid #ddd; border-collapse: collapse; }
    .main-table { width: 100%; margin: 24px auto 30px auto; background: #fff; border-radius: 10px; box-shadow: 0 2px 16px rgba(44,62,80,0.07); font-size: 1rem; }
    .main-table th, .main-table td { padding: 0.6rem 0.5rem; text-align: left; vertical-align: middle; }
    .main-table th { background: #8e44ad; color: #fff; font-weight: 600; }
    .main-table tr:nth-child(even) { background: #f9f7fa; }
    .main-table img { height: 48px; width: 48px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; }
    .actions .btn { margin: 0 2px; font-size: 0.85em; padding: 5px 12px; }
    .message, .error { max-width: 540px; margin: 20px auto; padding: 1rem; border-radius: 6px; text-align: center; }
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
    <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar" />
  </header>

  <!-- SIDEBAR -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <a href="${pageContext.request.contextPath}/view/AdminDashboard.jsp">Dashboard</a>
      <a href="#">Orders</a>
      <a href="${pageContext.request.contextPath}/view/Item.jsp" class="active">Item</a>
      <a href="${pageContext.request.contextPath}/users">Users</a>
      <a href="${pageContext.request.contextPath}/staff">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout">Logout</a>
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
        <form action="${pageContext.request.contextPath}/items" method="get" style="margin:0;">
          <input type="hidden" name="mainCategory" value="book"/>
          <button type="submit" class="${bookActive}">Books</button>
        </form>
        <form action="${pageContext.request.contextPath}/items" method="get" style="margin:0;">
          <input type="hidden" name="mainCategory" value="accessory"/>
          <button type="submit" class="${accessoryActive}">Accessories</button>
        </form>
      </div>
      <div style="display:flex; gap:6px;">
        <a href="${pageContext.request.contextPath}/items?mainCategory=book&showForm=1" class="btn">+ Add Book</a>
        <a href="${pageContext.request.contextPath}/items?mainCategory=accessory&showForm=1" class="btn">+ Add Accessory</a>
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
      <form id="itemForm" action="${pageContext.request.contextPath}/items" method="post" enctype="multipart/form-data">
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
            <label for="bookSub">Subcategory</label>
            <select id="bookSub" name="bookSub" required>
              <option value="">-- Select Subcategory --</option>
              <option value="Educational" <c:if test="${not empty editBook and editBook.category == 'Educational'}">selected</c:if>>Educational</option>
              <option value="Children's Books" <c:if test="${not empty editBook and editBook.category == \"Children's Books\"}">selected</c:if>>Children's Books</option>
              <option value="Fiction" <c:if test="${not empty editBook and editBook.category == 'Fiction'}">selected</c:if>>Fiction</option>
              <option value="Non-Fiction" <c:if test="${not empty editBook and editBook.category == 'Non-Fiction'}">selected</c:if>>Non-Fiction</option>
              <option value="Comics & Graphic Novels" <c:if test="${not empty editBook and editBook.category == 'Comics & Graphic Novels'}">selected</c:if>>Comics & Graphic Novels</option>
              <option value="Technology & Science" <c:if test="${not empty editBook and editBook.category == 'Technology & Science'}">selected</c:if>>Technology & Science</option>
              <option value="Language Learning" <c:if test="${not empty editBook and editBook.category == 'Language Learning'}">selected</c:if>>Language Learning</option>
              <option value="Art & Design" <c:if test="${not empty editBook and editBook.category == 'Art & Design'}">selected</c:if>>Art & Design</option>
              <option value="Cookbooks & Food" <c:if test="${not empty editBook and editBook.category == 'Cookbooks & Food'}">selected</c:if>>Cookbooks & Food</option>
              <option value="Poetry" <c:if test="${not empty editBook and editBook.category == 'Poetry'}">selected</c:if>>Poetry</option>
              <option value="Law" <c:if test="${not empty editBook and editBook.category == 'Law'}">selected</c:if>>Law</option>
              <option value="Sports & Outdoors" <c:if test="${not empty editBook and editBook.category == 'Sports & Outdoors'}">selected</c:if>>Sports & Outdoors</option>
            </select>
          </div>
          <div class="form-group">
            <label>Title *</label>
            <input type="text" name="title" value="<c:out value='${editBook.title}'/>" required />
          </div>
          <div class="form-group">
            <label>Author *</label>
            <input type="text" name="author" value="<c:out value='${editBook.author}'/>" required />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea name="descriptionBook" rows="3"><c:out value='${editBook.description}'/></textarea>
          </div>
          <div class="form-group">
            <label>Supplier Name *</label>
            <input type="text" name="supplierBook" value="<c:out value='${editBook.supplier}'/>" required />
          </div>
          <div class="form-group">
            <label>Upload Image</label>
            <input type="file" name="imgBook" accept="image/*" id="imgBookInput" />
            <div>
              <c:if test="${not empty editBook.imageUrl}">
                <div class="small-note">Current image:</div>
                <img src="${pageContext.request.contextPath}/${editBook.imageUrl}" alt="Current Book Image" class="preview-img" />
              </c:if>
              <div id="newBookPreviewContainer" style="margin-top:4px; display:none;">
                <div class="small-note">Selected image preview:</div>
                <img src="#" alt="New Book Preview" class="preview-img" id="newBookPreview" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>Minimum Stock Quantity *</label>
            <input type="number" name="minStockBook" value="<c:out value='${editBook.minStock}'/>" min="0" required />
          </div>
        </div>

        <!-- Accessory Fields -->
        <div id="accessoryFields" class="${mainCategory != 'accessory' ? 'hidden' : ''}">
          <div class="form-group">
            <label for="accSub">Subcategory</label>
            <select id="accSub" name="accSub">
              <option value="Writing Instruments" <c:if test="${not empty editAccessory and editAccessory.category == 'Writing Instruments'}">selected</c:if>>Writing Instruments</option>
              <option value="Erasers & Correction" <c:if test="${not empty editAccessory and editAccessory.category == 'Erasers & Correction'}">selected</c:if>>Erasers & Correction</option>
              <option value="Paper Products" <c:if test="${not empty editAccessory and editAccessory.category == 'Paper Products'}">selected</c:if>>Paper Products</option>
              <option value="Files & Folders" <c:if test="${not empty editAccessory and editAccessory.category == 'Files & Folders'}">selected</c:if>>Files & Folders</option>
            </select>
          </div>
          <div class="form-group">
            <label>Item Name *</label>
            <input type="text" name="itemName" value="<c:out value='${editAccessory.name}'/>" required />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea name="descriptionAcc" rows="3"><c:out value='${editAccessory.description}'/></textarea>
          </div>
          <div class="form-group">
            <label>Supplier Name *</label>
            <input type="text" name="supplierAcc" value="<c:out value='${editAccessory.supplier}'/>" required />
          </div>
          <div class="form-group">
            <label>Upload Image</label>
            <input type="file" name="imgAcc" accept="image/*" id="imgAccInput" />
            <div>
              <c:if test="${not empty editAccessory.imageUrl}">
                <div class="small-note">Current image:</div>
                <img src="${pageContext.request.contextPath}/${editAccessory.imageUrl}" alt="Current Accessory Image" class="preview-img" />
              </c:if>
              <div id="newAccPreviewContainer" style="margin-top:4px; display:none;">
                <div class="small-note">Selected image preview:</div>
                <img src="#" alt="New Accessory Preview" class="preview-img" id="newAccPreview" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>Minimum Stock Quantity *</label>
            <input type="number" name="minStockAcc" value="<c:out value='${editAccessory.minStock}'/>" min="0" required />
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
    <form class="search-box" action="${pageContext.request.contextPath}/items" method="get">
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
    </form>

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
              <th>Image</th>
              <th>Min Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="a" items="${accessories}" varStatus="loop">
              <tr>
                <td>${loop.index + 1}</td>
                <td>${a.name}</td>
                <td>${a.category}</td>
                <td style="max-width:180px;word-break:break-all;">${a.description}</td>
                <td>${a.supplier}</td>
                <td>
                  <c:if test="${not empty a.imageUrl}">
                    <img src="${pageContext.request.contextPath}/${a.imageUrl}" alt="Accessory Image" />
                  </c:if>
                </td>
                <td>${a.minStock}</td>
                <td class="actions">
                  <a class="btn" href="<c:url value='/items?action=edit&type=accessory&id=${a.accessoryId}&mainCategory=accessory'/>">Edit</a>
                  <a class="btn" href="<c:url value='/items?action=delete&type=accessory&id=${a.accessoryId}&mainCategory=accessory'/>" onclick="return confirm('Delete accessory?');">Delete</a>
                </td>
              </tr>
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
              <th>Image</th>
              <th>Min Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="b" items="${books}" varStatus="loop">
              <tr>
                <td>${loop.index + 1}</td>
                <td>${b.title}</td>
                <td>${b.author}</td>
                <td>${b.category}</td>
                <td style="max-width:180px;word-break:break-all;">${b.description}</td>
                <td>${b.supplier}</td>
                <td>
                  <c:if test="${not empty b.imageUrl}">
                    <img src="${pageContext.request.contextPath}/${b.imageUrl}" alt="Book Image" />
                  </c:if>
                </td>
                <td>${b.minStock}</td>
                <td class="actions">
                  <a class="btn" href="<c:url value='/items?action=edit&type=book&id=${b.bookId}&mainCategory=book'/>">Edit</a>
                  <a class="btn" href="<c:url value='/items?action=delete&type=book&id=${b.bookId}&mainCategory=book'/>" onclick="return confirm('Delete book?');">Delete</a>
                </td>
              </tr>
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

    // Enable/disable form controls to avoid hidden required fields triggering validation
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

    // Image preview helpers
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
    previewFile(document.getElementById('imgBookInput'), 'newBookPreview', 'newBookPreviewContainer');
    previewFile(document.getElementById('imgAccInput'), 'newAccPreview', 'newAccPreviewContainer');
  </script>
</body>
</html>





  