/* ================= SIDEBAR TOGGLE ================= */
const sidebar   = document.getElementById('sidebar');
const overlay   = document.getElementById('overlay');
const toggleBtn = document.getElementById('toggleSidebar');
const main      = document.getElementById('main');

function toggleSidebarFn(){
  if(!sidebar) return;
  sidebar.classList.toggle('open');
  overlay.classList.toggle('show');
  main.classList.toggle('shift');
}
toggleBtn && toggleBtn.addEventListener('click', toggleSidebarFn);
overlay   && overlay.addEventListener('click', toggleSidebarFn);

/* ================= STAFF PAGE LOGIC ================= */
function openEditModal(id, name, username, email, tel){
  document.getElementById('modalTitle').textContent = 'Update Staff';
  document.getElementById('f-action').value = 'update';
  document.getElementById('f-id').value = id;
  document.getElementById('f-name').value = name;
  document.getElementById('f-username').value = username;
  document.getElementById('f-email').value = email;
  document.getElementById('f-telephone').value = tel;
  document.getElementById('pwRow').style.display = 'none';
  document.getElementById('modal').classList.add('show');
}

const btnAddStaffEl = document.getElementById('btnAddStaff');
const cancelBtnEl   = document.getElementById('cancelBtn');
const modalEl       = document.getElementById('modal');

btnAddStaffEl && btnAddStaffEl.addEventListener('click', ()=>{
  document.getElementById('modalTitle').textContent = 'Add Staff';
  document.getElementById('f-action').value = 'add';
  document.getElementById('staffForm').reset();
  document.getElementById('pwRow').style.display = 'block';
  modalEl.classList.add('show');
});
cancelBtnEl && cancelBtnEl.addEventListener('click', ()=> modalEl.classList.remove('show'));
modalEl && modalEl.addEventListener('click', e=>{
  if(e.target===modalEl) modalEl.classList.remove('show');
});
