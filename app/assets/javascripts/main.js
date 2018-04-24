$(function(){

    // Right click on the logo
    $("#logo").on("contextmenu",function(e){
        e.preventDefault()
        e.stopPropagation()
        $("#getLogo")
            .fadeIn('fast')
            .click(function(){
                $(this).fadeOut("fast")
            })
        return false
    });


    // Scope on home video
    $("#video").each(function(i, el){
        $("a", el).on("click", function(e){
            e.preventDefault();
            el.className = this.className;
            $(el).addClass("playing");
            $("#videoframe").html('<iframe width="610" height="343" src="//www.youtube.com/embed/'+$(this).attr("data-id")+'?rel=0&autohide=1&autoplay=1&modestbranding=0&showinfo=1" frameborder="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>')
            return false;
        })

        // Extend UI
        $("#extend").hover(function(e){
            $("#content").addClass("plus")
            $("body").addClass("animate")
        },function(e){
            $("#content").removeClass("plus")
        }).click(function(e){
            $("#content").removeClass("plus")
            $("body").toggleClass("flex")
            if (window.localStorage) localStorage['flex'] = $("body").hasClass("flex")
        })
    });

    // Scope on documentation pages
    $("body.documentation").each(function(el){
        // Versions' dropdown
        $(".dropdown", el).on("click", "dt", function(e){
            $(e.delegateTarget).toggleClass("open")
        })

        // Extend UI
        $("#extend").hover(function(e){
            $("#content").addClass("plus")
            $("body").addClass("animate")
        },function(e){
            $("#content").removeClass("plus")
        }).click(function(e){
            $("#content").removeClass("plus")
            $("body").toggleClass("flex")
            if (window.localStorage) localStorage['flex'] = $("body").hasClass("flex")
        })
    });

    // Code snippet tabs
    $("body.documentation article dl").has("dd > pre").each(function() {
        var dl = $(this);
        dl.addClass("tabbed");
        dl.find("dt").each(function(i) {
            var dt = $(this);
            dt.html("<a href=\"#tab" + i + "\">" + dt.text() + "</a>");
        });
        dl.find("dd").hide();
        var current = dl.find("dt:first").addClass("current");
        var currentContent = current.next("dd").show();
        dl.css("height", current.height() + currentContent.height());
    });
    $("body.documentation article dl.tabbed dt a").click(function(e){
        e.preventDefault();
        var current = $(this).parent("dt");
        var dl = current.parent("dl");
        dl.find(".current").removeClass("current").next("dd").hide();
        current.addClass("current");
        var currentContent = current.next("dd").show();
        dl.css("height", current.height() + currentContent.height());
    });

    // Scope on modules page
    $("body.modules").each(function(el){
        var list = $("#list > li")
          , versions = $("input[name=version]")
          , filters = {
                keyword: ""
              , version: "v2"
            }

        function doFilter (){
            console.log("kw", filters.keywords)
            if (!filters.keywords) {
                list.show()
            } else {
                for (i in filters.keywords){
                    list.each(function(){
                        if ($(this).text().toLowerCase().search(filters.keywords[i].toLowerCase())>=0) {
                            $(this).show()
                        } else {
                            $(this).hide()
                        }
                    })
                }
            }
            list.each(function(){
                if ( !!filters.version && $(this).find("."+filters.version).length == 0 ) {
                    $(this).hide()
                }
            })

        }

        $("#module-search").keyup(function(e){
            filters.keywords = !this.value ? false : this.value.split(" ")
            doFilter()
        })

        $(versions).change(function(){
            filters.version = this.value
            doFilter()
        })
        doFilter()

        list.click(function(e){
            $(this).addClass("open")
                .siblings(".open").removeClass("open")
        })

    })

    // Scope on download page
    $("body.alternatives").each(function(el){
        // Show guides on download
        var download = $(".latest"),
            getStarted = $(".get-started"),
            getStartedBack = $(".back", getStarted);

        download.on('click', function(e) {
            getStarted.show();
        });
        getStartedBack.click(function(e) {
            getStarted.hide();
        });

        // Older releases
        var versions = $(".alternatives .version");

        versions.each(function(i, el) {
            var list = $(".release", el).slice(3).hide();
            $(".show-all-versions", el).click(function() {
                list.show();
                $(this).hide();
            }).toggle(!!list.length);
        });
    })

    // This function handles analytics tracking for certain
    // download links.
    function trackDownload(selector, name) {
        var downloadElements = $(selector);
        downloadElements.click(function() {
            var el = $(this)
            var version = el.data("version");
            if (version) {
                var label = name + "-" + version;
            } else {
                var label = name;
            }
            _gaq.push(["_trackEvent", "download", "click", label]);
            return true;
        })
        // Target downloads at an iframe so they don't unload this
        // page. Unloading the page can interrupt Google Analytics
        // and mess up our statistics.
        downloadElements.attr('target', 'download-iframe');
    }
    trackDownload(".downloadStandaloneLink", "standalone");
    trackDownload(".downloadDevelopmentLink", "development");
    trackDownload(".downloadPreviousLink", "previous");
})

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-37989507-1']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();

(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-23127719-1', 'lightbend.com', {'allowLinker': true, 'name': 'tsTracker'});
ga('require', 'linker');
ga('linker:autoLink', ['lightbend.com','playframework.com','scala-lang.org','scaladays.org','spray.io','akka.io','scala-sbt.org','scala-ide.org']);
ga('tsTracker.send', 'pageview');
