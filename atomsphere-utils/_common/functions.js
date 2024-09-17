function checkAll(bx) {
  var cbs = document.getElementsByTagName("input");
  for (var i = 0; i < cbs.length; i++) {
    var cb = cbs[i];
    var ctr = cb.closest("tr");
    if (
      cb.type == "checkbox" &&
      (!ctr.getAttribute("style") ||
        !ctr.getAttribute("style").includes("display: none;"))
    ) {
      cb.checked = bx.checked;
    }
  }
}

const MINUTE = 60;
const HOUR = MINUTE * 60;
const DAY = HOUR * 24;
const WEEK = DAY * 7;
const MONTH = DAY * 30;
const YEAR = DAY * 365;

function getTimeAgo(date) {
  if (date == "Invalid Date") {
    return "---";
  }

  const secondsAgo = Math.round((Date.now() - Number(date)) / 1000);

  if (secondsAgo < MINUTE) {
    return secondsAgo + ` second${secondsAgo !== 1 ? "s" : ""} ago`;
  }

  let divisor;
  let unit = "";

  if (secondsAgo < HOUR) {
    [divisor, unit] = [MINUTE, "minute"];
  } else if (secondsAgo < DAY) {
    [divisor, unit] = [HOUR, "hour"];
  } else if (secondsAgo < WEEK) {
    [divisor, unit] = [DAY, "day"];
  } else if (secondsAgo < MONTH) {
    [divisor, unit] = [WEEK, "week"];
  } else if (secondsAgo < YEAR) {
    [divisor, unit] = [MONTH, "month"];
  } else {
    [divisor, unit] = [YEAR, "year"];
  }

  const count = Math.floor(secondsAgo / divisor);
  return `${count} ${unit}${count > 1 ? "s" : ""} ago`;
}

function updateTimeAgo() {
  var els = document.getElementsByClassName("timeAgo");
  for (var i = 0; i < els.length; i++) {
    els[i].innerHTML = getTimeAgo(new Date(els[i].getAttribute("dateStr")));
  }
}

setInterval(updateTimeAgo, 10000);

document.addEventListener("componentsLoaded", function () {
  updateTimeAgo();
});

//document.addEventListener("loadEditor", function (event) {
//  console.log("111");
//  console.log(event.detail);
//});

function decode(input) {
  var txt = document.createElement("textarea");
  txt.innerHTML = input;
  return txt.value;
}

function makeTablesSortable() {
  const getCellValue = (tr, idx) =>
    tr.children[idx].innerText || tr.children[idx].textContent;

  const comparer = (idx, asc) => (a, b) =>
    ((v1, v2) =>
      v1 !== "" && v2 !== "" && !isNaN(v1) && !isNaN(v2)
        ? v1 - v2
        : v1.toString().localeCompare(v2))(
      getCellValue(asc ? a : b, idx),
      getCellValue(asc ? b : a, idx),
    );

  document.querySelectorAll("th").forEach((th) =>
    th.addEventListener("click", () => {
      const table = th.closest("table");
      const tbody = table.querySelector("tbody");
      Array.from(tbody.querySelectorAll("tr"))
        .sort(
          comparer(
            Array.from(th.parentNode.children).indexOf(th),
            (this.asc = !this.asc),
          ),
        )
        .forEach((tr) => tbody.appendChild(tr));
    }),
  );
}
