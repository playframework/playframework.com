$(function(){

    $(".try-option h3").on('click', function(el) {
        var content = $(this).parent().find(".try-option-content");
        $(content).slideToggle("fast");
    });

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
})