// Sidebar toggle
const sidebar   = document.getElementById('sidebar');
const overlay   = document.getElementById('overlay');
const toggleBtn = document.getElementById('toggleSidebar');
const main      = document.getElementById('main');

function toggleSidebarFn(){
  sidebar.classList.toggle('open');
  overlay.classList.toggle('show');
  main.classList.toggle('shift'); // optional push
}

toggleBtn.addEventListener('click', toggleSidebarFn);
overlay.addEventListener('click', toggleSidebarFn);

// Highlight active link (basic example)
document.querySelectorAll('#sidebar a').forEach(a=>{
  a.addEventListener('click', function(){
    document.querySelectorAll('#sidebar a').forEach(x=>x.classList.remove('active'));
    this.classList.add('active');
    toggleSidebarFn(); // close after click on mobile
  });
});
