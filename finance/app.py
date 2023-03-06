import os
from datetime import datetime

from cs50 import SQL
from flask import Flask, flash, redirect, render_template, request, session
from flask_session import Session
from tempfile import mkdtemp
from werkzeug.security import check_password_hash, generate_password_hash

from helpers import apology, login_required, lookup, usd

# Configure application
app = Flask(__name__)

# Custom filter
app.jinja_env.filters["usd"] = usd

# Configure session to use filesystem (instead of signed cookies)
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_TYPE"] = "filesystem"
Session(app)

# Configure CS50 Library to use SQLite database
db = SQL("sqlite:///finance.db")

# Make sure API key is set
if not os.environ.get("API_KEY"):
    raise RuntimeError("API_KEY not set")


@app.after_request
def after_request(response):
    """Ensure responses aren't cached"""
    response.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    response.headers["Expires"] = 0
    response.headers["Pragma"] = "no-cache"
    return response


@app.route("/")
@login_required
def index():
    """Show portfolio of stocks"""
    # SELECT user_id,share, SUM (num), time, price FROM purchase WHERE user_id = 2 GROUP BY(share);
    query = db.execute("SELECT share, SUM (num), symbol FROM purchase WHERE user_id = ? GROUP BY(share)", session["user_id"])

    money_owned = (db.execute("SELECT cash FROM users WHERE id = ?", session["user_id"]))[0]['cash']

    shares = []
    sumnums = []
    symbols = []
    prices = []
    total1 = []


    for row in query:
        shares.append(row["share"])
        sumnums.append(row["SUM (num)"])
        symbols.append(row["symbol"])
        share_price = lookup(row["symbol"])["price"]
        prices.append(share_price)
        total1.append(prices[-1] * sumnums[-1])

    # for symbol in symbols:
    for x in range(0, len(prices)):
        prices[x] = usd(prices[x])

    total2 = usd(money_owned + sum(total1))

    for x in range(0, len(total1)):
        total1[x] = usd(total1[x])

    money_owned = usd(money_owned)

    return render_template("index.html", len=len(shares), shares=shares, sumnums=sumnums, symbols=symbols, prices=prices,
    total1=total1, money_owned=money_owned, total2=total2)


    # return render_template("index.html", datas = [{"share": query[0]["share"], "num": query[0]["SUM (num)"], "symbol": query[0]["symbol"]}
    #  ])
    # return apology("TODO")

@app.route("/addmoney", methods=["GET", "POST"])
@login_required
def add_money():
    if request.method == "POST":
        amount = float(request.form.get("add"))
        db.execute("UPDATE users SET cash = cash + ? WHERE id = ?", amount, session["user_id"])
        return redirect("/")
    else:
        return render_template("addmoney.html")


@app.route("/buy", methods=["GET", "POST"])
@login_required
def buy():
    """Buy shares of stock"""
    if request.method == "POST":
        if not request.form.get("symbol") or not request.form.get("shares"):
            return apology("must provide both symbol and number of shares")
        if lookup(request.form.get("symbol")) == None:
            return apology("Invalid symbol")
        if int(request.form.get("shares")) <= 0 or not request.form.get("shares").isdigit():
            return apology("Number of shares must be an integer bigger than 0")

        share_name = lookup(request.form.get("symbol"))["name"]
        share_price = lookup(request.form.get("symbol"))["price"]
        share_num = request.form.get("shares")
        share_symbol = lookup(request.form.get("symbol"))["symbol"]

        price = int(share_num) * float(share_price)
        money_owned = (db.execute("SELECT cash FROM users WHERE id = ?", session["user_id"]))[0]['cash']
        # money_owned = money_owned_q[0]['cash']
        if price > money_owned:
            return apology("you don't have enough funds")

        for x in range(0, int(share_num)):
            db.execute("INSERT INTO purchase (user_id, share, num, time, price, symbol) VALUES (?, ?, ?, datetime('now', '+1 hour'), ?, ?)",
            session["user_id"], share_name, 1, share_price, share_symbol)

        # db.execute("INSERT INTO purchasehistory (user_id, share, num, time, symbol, price) VALUES (?, ?, ?, datetime('now', '+1 hour'), ?, ?)",
        # session["user_id"], share_num, share_price, share_symbol)
        db.execute("INSERT INTO purchasehistory (user_id, share, num, time, symbol, price) VALUES (?, ?, ?, datetime('now', '+1 hour'), ?, ?)",
        session["user_id"], share_name, share_num, share_symbol, usd(share_price))


        db.execute("UPDATE users SET cash = cash - ? WHERE id = ?", price, session["user_id"])

        return redirect("/")

    else:
        return render_template('buy.html')

    # return apology("TODO")


@app.route("/history")
@login_required
def history():
    """Show history of transactions"""
    query = db.execute("SELECT symbol, num, price, time FROM purchasehistory WHERE user_id = ?", session["user_id"])

    return render_template("history.html", query=query)

    # return apology("TODO")


@app.route("/login", methods=["GET", "POST"])
def login():
    """Log user in"""

    # Forget any user_id
    session.clear()

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":

        # Ensure username was submitted
        if not request.form.get("username"):
            return apology("must provide username", 403)

        # Ensure password was submitted
        elif not request.form.get("password"):
            return apology("must provide password", 403)

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username = ?", request.form.get("username"))

        # Ensure username exists and password is correct
        if len(rows) != 1 or not check_password_hash(rows[0]["hash"], request.form.get("password")):
            return apology("invalid username and/or password", 403)

        # Remember which user has logged in
        session["user_id"] = rows[0]["id"]

        # Redirect user to home page
        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("login.html")


@app.route("/register", methods=["GET", "POST"])
def register():
    """Register user""" #czy tu też dawać session clear jw.?
    if request.method == "POST":
        if not request.form.get("username") or not request.form.get("password") or not request.form.get("confirmation"):
            return apology("must provide username, password and confirmation -,-", 400)
        if request.form.get("password") != request.form.get("confirmation"):
            return apology("password and confirmation do not match!", 400)

        rows = db.execute("SELECT * FROM users WHERE username = ?", request.form.get("username"))
        if len(rows) > 0:
            return apology("username already taken", 400)

        password = generate_password_hash(request.form.get("password"))
        db.execute("INSERT INTO users (username, hash) VALUES (?, ?)", request.form.get("username"), password)

        session["user_id"] = db.execute("SELECT * FROM users WHERE username = ?", request.form.get("username"))[0]["id"]

        # session["user_id"] = db.execute("SELECT id FROM users")
        return redirect("/")

    else:
        return render_template("register.html")

    # return apology("TODO")

@app.route("/logout")
def logout():
    """Log user out"""

    # Forget any user_id
    session.clear()

    # Redirect user to login form
    return redirect("/")


@app.route("/quote", methods=["GET", "POST"])
@login_required
def quote():
    """Get stock quote."""
    if request.method == "POST":
        result = lookup(request.form.get("symbol"))
        if result:
            return render_template("quoted.html", name = result["name"], symbol = result["symbol"], price = usd(result["price"]))
        else:
            return apology("Invalid symbol")

    else:
        return render_template("quote.html")
    # return apology("TODO")



@app.route("/sell", methods=["GET", "POST"])
@login_required
def sell():
    """Sell shares of stock"""
    query = db.execute("SELECT share, symbol, price, SUM (num) FROM purchase WHERE user_id = ? GROUP BY share", session["user_id"])

    if request.method == "POST":
        if not request.form.get("symbol") or not request.form.get("shares"):
            return apology("Must provide both symbol and number of shares")
        if int(request.form.get("shares")) <= 0:
            return apology("Must provide a positive number of shares")
        if int(request.form.get("shares")) > db.execute("SELECT SUM (num) FROM purchase WHERE user_id = ? AND symbol = ?",
        session["user_id"], request.form.get("symbol"))[0]['SUM (num)']:
            return apology("You don't have that many shares")

        share_price = lookup(request.form.get("symbol"))["price"]
        share_num = request.form.get("shares")
        share_symbol = lookup(request.form.get("symbol"))["symbol"]
        price = int(share_num) * float(share_price)
        share_name = lookup(request.form.get("symbol"))["name"]
        minus_share_num = int(share_num) * (-1)

        db.execute("UPDATE users SET cash = cash + ? WHERE id = ?", price, session["user_id"])

        for x in range(0, int(share_num)):
            rowid = db.execute("SELECT rowid FROM purchase WHERE user_id = ? AND symbol = ?", session["user_id"], request.form.get("symbol"))[0]['rowid']
            db.execute("DELETE FROM purchase WHERE user_id = ? AND ROWID = ? AND symbol = ?", session["user_id"], rowid, request.form.get("symbol"))

        db.execute("INSERT INTO purchasehistory (user_id, share, num, time, symbol, price) VALUES (?, ?, ?, datetime('now', '+1 hour'), ?, ?)",
        session["user_id"], share_name, minus_share_num, share_symbol, usd(share_price))

        return redirect("/")
    else:
        # shares_owned = []
        # for el in query:
        #     shares_owned.append(el['share'])  to też dobrze jkbc
        return render_template("sell.html", query=query)

    # return apology("TODO")


        # shares_owned = db.execute("SELECT share, SUM (num), symbol FROM purchase WHERE user_id = ? GROUP BY(share)", session["user_id"])
        # share_names = []

        # for dic in range(0, len(shares_owned)):
        #     share_names.append(shares_owned[dic]["share"])