<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.pahanaedu.model.*" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect(request.getContextPath()+"/login.jsp"); return; }
    @SuppressWarnings("unchecked")
    List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
    if (cart == null) cart = new ArrayList<>();

    int cartCount = (session.getAttribute("cartItemCount") instanceof Number)
            ? ((Number)session.getAttribute("cartItemCount")).intValue() : 0;

    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Your Cart | Pahana Edu</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    :root { --purple:#8e44ad; --light:#ecf0f1; }
    * { box-sizing:border-box; margin:0; padding:0; }
    body { font-family:Arial, sans-serif; background:var(--light); color:#2c3e50; }
    .navbar { background:var(--purple); color:#fff; padding:14px 24px; display:flex; align-items:center; justify-content:space-between; position:sticky; top:0; z-index:10; }
    .navbar a { color:#fff; text-decoration:none; margin-right:18px; }
    .navbar .badge { background:#e74c3c; color:#fff; padding:2px 8px; border-radius:999px; font-size:12px; margin-left:6px; }
    .container { max-width:1100px; margin:24px auto; padding:0 16px; }
    h1 { color:var(--purple); margin-bottom:12px; }
    .alert { padding:12px 14px; border-radius:8px; margin-bottom:14px; }
    .alert-success { background:#d8f5dd; color:#165c26; border:1px solid #b8e8c3; }
    .cart-layout { display:grid; grid-template-columns: 1.5fr 1fr; gap:20px; }
    @media (max-width:900px){ .cart-layout{ grid-template-columns:1fr; } }
    .card { background:#fff; border-radius:12px; box-shadow:0 2px 10px rgba(0,0,0,.06); overflow:hidden; }
    .card h2 { background:linear-gradient(135deg, var(--purple), #9b59b6); color:#fff; padding:14px 16px; font-size:18px; }
    table { width:100%; border-collapse:collapse; }
    th, td { padding:12px 10px; border-bottom:1px solid #f0f0f0; vertical-align:middle; }
    th { text-align:left; color:#7f8c8d; }
    .qty-form { display:flex; align-items:center; gap:8px; }
    .qty-input { width:64px; padding:6px; border:2px solid #ecf0f1; border-radius:8px; text-align:center; }
    .btn { border:0; cursor:pointer; padding:10px 14px; border-radius:10px; }
    .btn-purple { background:var(--purple); color:#fff; }
    .btn-ghost { background:#f0ebf4; color:#4b2c59; }
    .btn-danger { background:#e74c3c; color:#fff; }
    .right { text-align:right; }
    .summary { padding:14px 16px; }
    .summary .row { display:flex; justify-content:space-between; margin:8px 0; }
    .summary .total { font-weight:bold; font-size:18px; color:var(--purple); }
    .form { padding:14px 16px; display:grid; gap:10px; }
    .form label { font-size:13px; color:#7f8c8d; }
    .form input, .form textarea {
      width:100%; padding:10px; border:2px solid #ecf0f1; border-radius:10px;
    }
    .form textarea{ resize:vertical; min-height:70px; }
    .empty { padding:22px; color:#7f8c8d; text-align:center; }
  </style>
</head>
<body>
  <div class="navbar">
    <div>
      <a href="<%=ctx%>/UserDashboardController" style="font-weight:bold;">Pahana Edu BookStore</a>
      <a href="<%=ctx%>/UserDashboardController">Shop</a>
      <a href="<%=ctx%>/blog.jsp">Blog</a>
      <a href="<%=ctx%>/contact.jsp">Contact</a>
    </div>
    <div>
      <span>Hi, <strong><%=user.getUsername()%></strong></span>
      <a href="<%=ctx%>/cart" style="margin-left:12px;">🛒 Cart <span class="badge"><%=cartCount%></span></a>
      <a href="<%=ctx%>/logout" style="margin-left:12px;">Log out</a>
    </div>
  </div>

  <div class="container">
    <h1>Shopping Cart</h1>

    <% if (request.getAttribute("message") != null) { %>
      <div class="alert alert-success"><%= request.getAttribute("message") %></div>
    <% } %>

    <div class="cart-layout">
      <!-- Left: Items -->
      <div class="card">
        <h2>Items</h2>
        <% if (cart.isEmpty()) { %>
          <div class="empty">Your cart is empty. Go to the <a href="<%=ctx%>/UserDashboardController">shop</a> to add items.</div>
        <% } else { %>
          <table>
            <thead>
              <tr>
                <th style="width:45%;">Item</th>
                <th>Type</th>
                <th class="right">Price (Rs.)</th>
                <th class="right">Qty</th>
                <th class="right">Subtotal</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <%
                double total = 0.0;
                for (CartItem ci : cart) {
                  double sub = ci.getSubtotal(); total += sub;
              %>
              <tr>
                <td><%=ci.getItemName()%></td>
                <td><%=ci.getItemType()%></td>
                <td class="right"><%=String.format("%.2f", ci.getPrice())%></td>
                <td class="right">
                  <form class="qty-form" method="post" action="<%=ctx%>/cart">
                    <input type="hidden" name="action" value="updateQty">
                    <input type="hidden" name="itemType" value="<%=ci.getItemType()%>">
                    <input type="hidden" name="itemId" value="<%=ci.getItemId()%>">
                    <input class="qty-input" type="number" min="0" name="quantity" value="<%=ci.getQuantity()%>">
                    <button class="btn btn-ghost" type="submit">Update</button>
                  </form>
                </td>
                <td class="right"><%=String.format("%.2f", sub)%></td>
                <td class="right">
                  <form method="post" action="<%=ctx%>/cart">
                    <input type="hidden" name="action" value="remove">
                    <input type="hidden" name="itemType" value="<%=ci.getItemType()%>">
                    <input type="hidden" name="itemId" value="<%=ci.getItemId()%>">
                    <input type="hidden" name="quantity" value="0">
                    <button class="btn btn-danger" type="submit">Remove</button>
                  </form>
                </td>
              </tr>
              <% } %>
            </tbody>
          </table>
        <% } %>
      </div>

      <!-- Right: Summary + Customer -->
      <div class="card">
        <h2>Checkout</h2>

        <div class="summary">
          <div class="row"><span>Items</span><span><%=cart.size()%></span></div>
          <div class="row total">
            <%
              double grand = 0.0;
              for (CartItem ci : cart) grand += ci.getSubtotal();
            %>
            <span>Total</span><span>Rs. <%=String.format("%.2f", grand)%></span>
          </div>
        </div>

        <form class="form" method="post" action="<%=ctx%>/OnlineOrderController">
          <label>Customer Name</label>
          <input name="customerName" value="<%=user.getUsername()==null? "":user.getUsername()%>">

          <label>Phone</label>
          <input name="phone" value="<%=user.getTelephone()==null? "":user.getTelephone()%>">

          <label>Email</label>
          <input type="email" name="email" value="<%=user.getEmail()==null? "":user.getEmail()%>">

          <label>Address</label>
          <textarea name="address"><%=user.getAddress()==null? "":user.getAddress()%></textarea>

          <button class="btn btn-purple" type="submit" <%= cart.isEmpty() ? "disabled" : "" %>>
            Place Order
          </button>
        </form>
      </div>
    </div>
  </div>
</body>
</html>
