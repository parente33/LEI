const todos = [
  'Learn a new course',
  'Read a book',
  'Go to the gym',
  'Go shopping'
];

/* Ex. 1: Add a event listener that triggers when the DOM is loaded here */
document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('list-date').innerHTML = new Date().toLocaleDateString();
  renderTodoList();
});

/* Ex. 2: Complete todo rendering */
// complete function to remove all child nodes
// arg parent is the node to clean
function removeAllChildNodes(parent) {
  while (parent.lastChild) {
    parent.removeChild(parent.lastChild);
  }
}

// render todo array here
function renderTodoList() {
  const todoList = document.getElementById("todo-list");
  removeAllChildNodes(todoList);

  todos.forEach(t => {
    const listItem = document.createElement("li");
    listItem.className = "todo-list-item";

    const para = document.createElement("p");
    para.textContent = t;

    const deleteButton = document.createElement("button");
    deleteButton.className = "delete-button";
    deleteButton.textContent = 'Delete';
    deleteButton.addEventListener('click', removeTodoItem);

    listItem.appendChild(para);
    listItem.appendChild(deleteButton);

    todoList.appendChild(listItem);
  })
}

/* Ex. 3: Add a event listener to element 'todo-form'*/
document.getElementById('todo-form').addEventListener('submit', event => {
  event.preventDefault();

  const input = document.getElementById('task-input');
  const taskText = input.value.trim();

  if (taskText != "") {
    if (!todos.includes(taskText)) {
      todos.push(taskText);
    }
  }

  input.value = "";

  renderTodoList(); 
});

/* Ex. 4 and 5: complete delete button click logic */
// arg event is the triggered event (with event you can get the element clicked).
function removeTodoItem(event) {
  const button = event.target;
  const p = button.previousElementSibling;
  const li = button.parentElement;

  const t = p.innerHTML;

  const index = todos.indexOf(t);
  if (index !== -1) todos.splice(index, 1);

  button.removeEventListener('click', removeTodoItem);
  li.remove();
}