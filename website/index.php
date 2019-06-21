<!DOCTYPE html>
<html lang="en">

<?php include('includes/head.php') ?>

<body>
    <?php include('includes/nav.php') ?>

    <div class="content">
        <h1>Ruinscraft Punishments</h1>
        <div class="search">
            <input id="username-search" type="text" placeholder="Enter a username">
        </div>

        <hr>

        <div class="recent">
            <div id="recent-warns">
                <?php include('includes/recent.php') ?>
            </div>
            <div id="recent-mutes">
                <?php include('includes/recent.php') ?>
            </div>
            <div id="recent-bans">
                <?php include('includes/recent.php') ?>
            </div>
        </div>

    </div>

    <?php include('includes/footer.php') ?>
</body>

</html>