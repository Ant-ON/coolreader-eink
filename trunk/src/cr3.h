
#ifndef _CR3_H_
#define _CR3_H_

#include "view.h"
#include "histlist.h"

/**
 * @short Application Main Window
 * @author Vadim Lopatin <vadim.lopatin@coolreader.org>
 * @version 0.1
 */

class 
cr3app : public wxApp
{
	public:
		virtual bool OnInit();
		virtual int OnExit();
};

class
cr3scroll : public wxScrollBar
{
private:
	cr3view *  _view;
public:
	cr3scroll( cr3view * view )
		: _view( view ) { }
    void OnSetFocus( wxFocusEvent& event );
private:
	DECLARE_EVENT_TABLE()
};

enum active_mode_t {
    am_none,
    am_book,
    am_history
};

class 
cr3Frame : public wxFrame
{
    private:
        bool _isFullscreen;
        active_mode_t _activeMode;
	public:
		cr3Frame( const wxString& title, const wxPoint& p, const wxSize& sz, lString16 appDir );

        void SetActiveMode( active_mode_t mode );
        void UpdateToolbar();

		void OnQuit( wxCommandEvent& event );
		void OnAbout( wxCommandEvent& event );
        void OnScroll( wxScrollEvent& event );
        void OnKeyDown( wxKeyEvent& event );
        void OnSetFocus( wxFocusEvent& event );
        void OnFileOpen( wxCommandEvent& event );
        void OnFileSave( wxCommandEvent& event );
        void OnCommand( wxCommandEvent& event );
        void OnShowTOC( wxCommandEvent& event );
        void OnShowHistory( wxCommandEvent& event );
        void OnUpdateUI( wxUpdateUIEvent& event );
        void OnClose( wxCloseEvent& event );
        void OnMouseWheel( wxMouseEvent& event);
        void OnSize( wxSizeEvent& event);
        void OnInitDialog( wxInitDialogEvent& event);
        void OnHistItemActivated( wxListEvent& event );

	protected:
    	cr3scroll * _scrollBar;
		cr3view * _view;
    	HistList * _hist;
        wxBoxSizer * _sizer;
        lString16 _appDir;
	private:
		DECLARE_EVENT_TABLE()
};

enum
{
	Menu_File_Quit = 100,
	Menu_File_About,
    Menu_View_ZoomIn,
    Menu_View_ZoomOut,
    Menu_View_NextPage,
    Menu_View_PrevPage,
    Menu_View_NextLine,
    Menu_View_PrevLine,
    Menu_View_Begin,
    Menu_View_End,
    Menu_View_ToggleFullScreen,
    Menu_View_TogglePages,
    Menu_View_TogglePageHeader,
    Menu_View_TOC,
    Menu_View_History,
};

enum
{
	Window_Id_Scrollbar = 1000,
	Window_Id_View,
    Window_Id_HistList
};

#endif // _CR3_H_
