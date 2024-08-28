function executeSearchOpt() {
  let opts = document.getElementsByName("opt")[0];
  let selection = opts.options[opts.selectedIndex].text;

  let deployed = document.getElementsByClassName("deployed");
  let not_deployed = document.getElementsByClassName("not-deployed");

  if (selection == "All Components") {
    for (let d of deployed) {
      d.removeAttribute("style");
    }
    for (let nd of not_deployed) {
      nd.removeAttribute("style");
    }
  } else if (selection == "Deployed Only") {
    for (let d of deployed) {
      d.removeAttribute("style");
    }
    for (let nd of not_deployed) {
      nd.style.display = "none";
    }
  } else if (selection == "Not Deployed Only") {
    for (let d of deployed) {
      d.style.display = "none";
    }
    for (let nd of not_deployed) {
      nd.removeAttribute("style");
    }
  }
}
